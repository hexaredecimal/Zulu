package com.zulu.runtime;

import com.zulu.utils.Clause;
import com.zulu.utils.AST;
import com.zulu.utils.ZuluException;
import com.zulu.utils.Function;
import com.zulu.utils.CallStack;
import com.zulu.patterns.ListPattern;
import com.zulu.patterns.ConsPattern;
import com.zulu.patterns.PackPattern;
import com.zulu.patterns.ConstantPattern;
import com.zulu.patterns.Pattern;
import com.zulu.patterns.VariablePattern;
import com.zulu.ast.PackAST;
import com.zulu.ast.ListAST;
import com.zulu.ast.ConsAST;
import com.zulu.ast.ExtractBindAST;
import com.zulu.ast.StringAST;
import com.zulu.ast.ConstantAST;
import com.zulu.ast.BindAST;
import com.zulu.optimizations.Optimization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserFunction implements Function, Serializable {

	public ArrayList<Clause> clauses;

	public UserFunction(ArrayList<Clause> clauses) {
		this.clauses = clauses;
	}

	@Override
	public ZuluValue execute(ZuluValue... args) {
		Table.push();
		try {
			boolean br = false;
			AST toExecute = null;
			ArrayList<String> toDelete = new ArrayList<>();
			for (int i = 0; i < clauses.size(); i++) {
				Clause clause = clauses.get(i);
				ArrayList<Pattern> patterns = patterns(clause.getArgs());
				if (patterns.size() != args.length) {
					continue;
				}
				if (patterns.isEmpty() && args.length == 0) {
					if (clause.getGuard() != null) {
						if ((clause.getGuard().execute()).toString().equals("true")) ; else {
							continue;
						}
					}
					toExecute = clause.getResult();
					break;
				}
				for (int k = 0; k < patterns.size(); k++) {
					Pattern pattern = patterns.get(k);
					ZuluValue arg = null;
					try {
						arg = args[k];
					} catch (ArrayIndexOutOfBoundsException ex) {
						break;
					}
					if (pattern instanceof VariablePattern p) {
						Table.define(p.getVariable(), arg);
						toDelete.add(p.getVariable());
					} else if (pattern instanceof ConstantPattern p) {
						boolean isEquals = p.getConstant().equals(arg);
						if (isEquals) ; else {
							br = true;
							break;
						}
					} else if (pattern instanceof ListPattern p) {
						if (!(arg instanceof ZuluList)) {
							br = true;
							break;
						}
						br = !(processList(p, arg, toDelete));
						if (br) {
							break;
						}
					} else if (pattern instanceof ConsPattern p) {
						if (!(arg instanceof ZuluList)) {
							br = true;
							break;
						}
						Table.set(p.getLeft(), head((ZuluList) arg));
						Table.set(p.getRight(), tail((ZuluList) arg));
					} else if (pattern instanceof PackPattern p) {
						List<ZuluValue> rest = null;
						//System.out.println("i: " + i);
						try {
							rest = List.of(args).subList(i, args.length);
						} catch (IndexOutOfBoundsException ex) {
							rest = new ArrayList<>();
						}
						LinkedList<ZuluValue> arr = new LinkedList<>();
						for (ZuluValue val : rest) {
							i += 1;
							arr.add(val);
						}
						Table.define(p.toString(), new ZuluList(arr));
						break;
					}
				}
				if (clause.getGuard() != null) {
					if ((clause.getGuard().execute()).toString().equals("true")) ; else {
						br = true;
					}
				}

				if (br) {
					Table.pop(); // Pop last args
					Table.push(); // Push new scope
					br = false;
					continue;
				}
				toExecute = clause.getResult();
				break;
			}
			if (toExecute == null) {
				throw new ZuluException("FunctionClause", "can't find function clause for args " + List.of(args) + " with clauses:\n   " + clauses);
			}
			ZuluValue result = toExecute.execute();
			Table.pop();
			return result;
		} catch (ZuluException ex) {
			CallStack.exit();
			throw ex;
		}
	}

	public void optimize(Optimization opt) {
		ArrayList<Clause> res = new ArrayList<>();
		for (Clause cl : clauses) {
			res.add(cl.optimize(opt));
		}
		clauses = res;
	}

	private boolean processList(ListPattern pattern, ZuluValue val, ArrayList<String> toDelete) {
		if (!((val instanceof ZuluList list))) {
			throw new ZuluException("BadArg", "expected list in list pattern");
		}
		if (list.getList().size() != pattern.getArr().size()) {
			return false;
		}
		for (int i = 0; i < pattern.getArr().size(); i++) {
			Pattern p = pattern(pattern.getArr().get(i));
			ZuluValue obj = list.getList().get(i);
			if (p instanceof VariablePattern c) {
				Table.define(c.getVariable(), obj);
				toDelete.add(c.getVariable());
			} else if (p instanceof ConstantPattern c) {
				if (!(c.getConstant().equals(obj))) {
					return false;
				}
			} else if (p instanceof ListPattern c) {
				if (processList(c, obj, toDelete)) {
				} else {
					return false;
				}
			} else if (p instanceof ConsPattern c) {
				if (!(obj instanceof ZuluList)) {
					return false;
				}
				Table.set(c.getLeft(), head((ZuluList) obj));
				Table.set(c.getRight(), tail((ZuluList) obj));
			}
		}
		return true;
	}

	private ArrayList<Pattern> patterns(ArrayList<AST> asts) {
		ArrayList<Pattern> result = new ArrayList<>();
		for (AST node : asts) {
			result.add(pattern(node));
		}
		return result;
	}

	private Pattern pattern(AST ast) {
		if (ast instanceof ExtractBindAST) {
			return new VariablePattern(ast.toString());
		} else if (ast instanceof ConstantAST) {
			return new ConstantPattern(ast.execute());
		} else if (ast instanceof BindAST) {
			return new ConstantPattern(ast.execute());
		} else if (ast instanceof ListAST list) {
			return new ListPattern(list.getArray());
		} else if (ast instanceof ConsAST cons) {
			return new ConsPattern(cons.getLeft().toString(), cons.getRight().toString());
		} else if (ast instanceof PackAST p) {
			return new PackPattern(p.name);
		} else if (ast instanceof StringAST) {
			return new ConstantPattern(ast.execute());
		}
		throw new ZuluException("BadMatch", "invalid pattern in function clause");
	}

	private LinkedList<Pattern> pattern(ListPattern pattern) {
		LinkedList<AST> asts = pattern.getArr();
		LinkedList<Pattern> patterns = new LinkedList<>();
		for (AST ast : asts) {
			patterns.add(pattern(ast));
		}

		return patterns;
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

	public ArrayList<Clause> getClauses() {
		return clauses;
	}

	private int addAtom(String atom) {
		return AtomTable.put(atom);
	}

	@Override
	public String toString() {
		return "#Function" + clauses;
	}
}
