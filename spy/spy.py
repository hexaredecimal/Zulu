# Generated by SPY

def do_fac(acc, mult) :
	if mult  ==  1 :
		return  acc
	
	return  do_fac(acc  *  mult, mult  -  1)


def fac(n) :
	return  do_fac(1, n)


(print(fac(5)))