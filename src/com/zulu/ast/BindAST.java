package com.zulu.ast;

import com.zulu.patterns.ListPattern;
import com.zulu.patterns.ConsPattern;
import com.zulu.patterns.ConstantPattern;
import com.zulu.patterns.Pattern;
import com.zulu.patterns.VariablePattern;
import com.zulu.Main;
import com.zulu.optimizations.Optimization;
import com.zulu.optimizations.VariableInfo;
import com.zulu.runtime.ZuluList;
import com.zulu.runtime.Table;
import com.zulu.utils.AST;
import com.zulu.utils.ZuluException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.zulu.runtime.ZuluValue;

public class BindAST implements AST {

	private static final long serialVersionUID = 1L;
	private final int line;
	private final String current;
	public AST left, right;

	public BindAST(AST left, AST right, int line, String current) {
		this.left = left;
		this.right = right;
		this.line = line;
		this.current = current;
	}

	@Override
	public ZuluValue execute() {
		Pattern pattern = pattern(left);
		return processPattern(pattern, right);
	}

	@Override
	public void visit(Optimization optimization) {
		right = optimization.optimize(right);
	}

	private ZuluValue processPattern(Pattern pattern, AST r) {
		ZuluValue ast = r.execute();
		if (pattern instanceof ListPattern) {
			if (!(ast instanceof ZuluList)) {
				Main.error("BadMatch", "no match of right-side value: " + ast, line, current);
			}
			ListPattern p = (ListPattern) pattern;
			LinkedList<ZuluValue> list = ((ZuluList) ast).getList();
			LinkedList<Pattern> patterns = pattern(p);
			for (int i = 0; i < list.size(); i++) {
				Pattern pattern1 = patterns.get(i);
				ZuluValue right = list.get(i);
				if (pattern1 instanceof VariablePattern c) {
					Table.set(c.getVariable(), right);
				} else if (pattern1 instanceof ConstantPattern) {
					ZuluValue l = ((ConstantPattern) pattern1).getConstant();
					if (right.equals(l)) ; else {
						Main.error("BadMatch", "no match of right-side value: " + ast, line, current);
					}
				} else if (pattern1 instanceof ListPattern) {
					if (!((right instanceof ZuluList))) {
						Main.error("BadMatch", "no match of right-side value: " + ast, line, current);
					}
					processPattern(pattern1, new ConstantAST(right));
				} else if (pattern1 instanceof ConsPattern p1) {
					if (!(right instanceof ZuluList)) {
						Main.error("BadMatch", "no match of right-side value: " + ast, line, current);
					}
					Table.set(p1.getLeft(), head((ZuluList) right));
					Table.set(p1.getRight(), tail((ZuluList) right));
				}
			}
			return ast;
		}
		if (pattern instanceof VariablePattern) {
			VariablePattern c = (VariablePattern) pattern;
			Table.set(c.getVariable(), ast);
		} else if (pattern instanceof ConstantPattern) {
			ZuluValue l = ((ConstantPattern) pattern).getConstant();
			if (ast.equals(l)) ; else {
				Main.error("BadMatch", "no match of right-side value: " + ast, line, current);
			}
		} else if (pattern instanceof ConsPattern) {
			ConsPattern p = (ConsPattern) pattern;
			if (!(ast instanceof ZuluList)) {
				Main.error("BadMatch", "no match of right-side value: " + ast, line, current);
			}
			Table.set(p.getLeft(), head((ZuluList) ast));
			Table.set(p.getRight(), tail((ZuluList) ast));
		}
		return ast;
	}

	private Pattern pattern(AST ast) {
		if (ast instanceof ExtractBindAST) {
			return new VariablePattern(ast.toString());
		} else if (ast instanceof ConstantAST) {
			return new ConstantPattern(ast.execute());
		} else if (ast instanceof BindAST) {
			return new ConstantPattern(ast.execute());
		} else if (ast instanceof ListAST) {
			return new ListPattern(((ListAST) ast).getArray());
		} else if (ast instanceof ConsAST) {
			ConsAST cons = (ConsAST) ast;
			return new ConsPattern(cons.getLeft().toString(), cons.getRight().toString());
		} else {
			Main.error("BadMatch", "invalid pattern in match ast", line, current);
		}
		return null;
	}

	private LinkedList<Pattern> pattern(ListPattern pattern) {
		LinkedList<AST> asts = pattern.getArr();
		LinkedList<Pattern> patterns = new LinkedList<>();
		for (AST ast : asts) {
			patterns.add(pattern(ast));
		}
		return patterns;
	}

	public HashMap<String, VariableInfo> emulate(HashMap<String, VariableInfo> storage, HashMap<String, Integer> mods) {
		Pattern root = pattern(left);
		try {
			if (root instanceof VariablePattern v) {
				if (mods.containsKey(v.getVariable()) && canEvalNow(right, storage, mods)) {
					AST tr = transform(right, storage, mods);
					mods.put(
						v.getVariable(),
						mods.get(v.getVariable()) + 1
					);
					storage.put(
						v.getVariable(),
						new VariableInfo(tr.execute(), mods.get(v.getVariable()))
					);
				} else if (canEvalNow(right, storage, mods)) {
					for (Map.Entry<String, VariableInfo> entry : storage.entrySet()) {
						if (entry.getValue().modifications != 0) {
							continue;
						}
						ZuluValue n = entry.getValue().value;
						Table.define(entry.getKey(), n);
					}
					mods.put(v.getVariable(), 0);
					storage.put(
						v.getVariable(),
						new VariableInfo(
							right.execute(),
							mods.get(v.getVariable())
						)
					);
					Table.clear();
				}
			} else if (root instanceof ConstantPattern) {
				;
			} else if (root instanceof ListPattern) {
				emulate_list(storage, mods, root);
			} else if (root instanceof ConsPattern) {

			}
		} catch (ZuluException ex) {

		}
		return storage;
	}

	private AST transform(AST a, HashMap<String, VariableInfo> info, HashMap<String, Integer> mods) {
		if (!(a instanceof ExtractBindAST)) {
			return a;
		}
		ExtractBindAST ast = (ExtractBindAST) a;
		if (!(info.containsKey(ast.toString()))) {
			return a;
		}
		AST result = new ConstantAST(info.get(ast.toString()).value);
		return result;
	}

	private boolean canEvalNow(AST ast, HashMap<String, VariableInfo> storage, HashMap<String, Integer> mods) {
		if (ast instanceof BinaryAST p) {
			boolean l = canEvalNow(p.expr1, storage, mods);
			boolean r = canEvalNow(p.expr2, storage, mods);
			return l && r;
		} else if (ast instanceof ConstantAST) {
			return true;
		} else if (ast instanceof UnaryAST p) {
			return canEvalNow(p.expr1, storage, mods);
		} else if (ast instanceof ListAST p) {
			for (AST node : p.getArray()) {
				if (canEvalNow(node, storage, mods)) {
					continue;
				}
				return false;
			}
			return true;
		} else if (ast instanceof TernaryAST p) {
			boolean t = canEvalNow(p.term, storage, mods);
			boolean l = canEvalNow(p.left, storage, mods);
			boolean r = canEvalNow(p.right, storage, mods);
			return t && l && r;
		} else if (ast instanceof ConsAST p) {
			boolean l = canEvalNow(p.left, storage, mods);
			boolean r = canEvalNow(p.right, storage, mods);
			return l && r;
		} else if (ast instanceof ExtractBindAST p) {
			boolean result = storage.containsKey(p.toString());
			return result;
		}
		return false;
	}

	private void emulate_list(HashMap<String, VariableInfo> storage, HashMap<String, Integer> mods, Pattern root) {
		ZuluValue r = right.execute();
		ListPattern p = (ListPattern) root;
		LinkedList<ZuluValue> list = ((ZuluList) r).getList();
		LinkedList<Pattern> patterns = pattern(p);
		for (int i = 0; i < list.size(); i++) {
			Pattern pattern1 = patterns.get(i);
			ZuluValue right = list.get(i);
			if (pattern1 instanceof VariablePattern) {
				if (mods.containsKey(((VariablePattern) pattern1).getVariable())) {
					storage.put(((VariablePattern) pattern1).getVariable(), new VariableInfo(right, mods.get(((VariablePattern) pattern1).getVariable())));
					mods.put(((VariablePattern) pattern1).getVariable(), mods.get(((VariablePattern) pattern1).getVariable()) + 1);
				} else {
					mods.put(((VariablePattern) pattern1).getVariable(), 0);
					storage.put(((VariablePattern) pattern1).getVariable(), new VariableInfo(right, mods.get(((VariablePattern) pattern1).getVariable())));
				}
			} else if (pattern1 instanceof ConstantPattern) {
				continue;
			} else if (pattern1 instanceof ListPattern p1) {
				emulate_list(storage, mods, p1);
				processPattern(pattern1, new ConstantAST(right));
			} else if (pattern1 instanceof ConsPattern p1) {
				if (mods.containsKey(p1.getLeft()) && mods.containsKey(p1.getRight())) {
					storage.put(p1.getLeft(), new VariableInfo(head((ZuluList) right), mods.get(p1.getLeft()) + 1));
					storage.put(p1.getRight(), new VariableInfo(tail((ZuluList) right), mods.get(p1.getRight()) + 1));
					mods.put(p1.getLeft(), mods.get(p1.getLeft()) + 1);
					mods.put(p1.getRight(), mods.get(p1.getRight()) + 1);
				} else {
					mods.put(p1.getLeft(), 0);
					mods.put(p1.getRight(), 0);
					storage.put(p1.getLeft(), new VariableInfo(head((ZuluList) right), mods.get(p1.getLeft()) + 1));
					storage.put(p1.getRight(), new VariableInfo(tail((ZuluList) right), mods.get(p1.getRight()) + 1));
				}
			}
		}
	}

	private ZuluValue head(ZuluList list) {
		return list.getList().get(0);
	}

	private ZuluValue tail(ZuluList list) {
		List<ZuluValue> arr = list.getList().subList(1, list.getList().size());
		LinkedList<ZuluValue> result = new LinkedList<>();
		for (ZuluValue val : arr) {
			result.add(val);
		}
		return new ZuluList(result);
	}

	@Override
	public String toString() {
		return String.format("%s = %s", left, right);
	}
}
