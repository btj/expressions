package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

abstract class Expression {
	static Literal evaluate(Expression e) {
		if (e instanceof Literal)
			return (Literal)e;
		BinaryOperatorExpression e_ = (BinaryOperatorExpression)e;
		Literal v1 = evaluate(e_.e1);
		Literal v2 = evaluate(e_.e2);
		if (e_ instanceof AddExpression) {
			if (v1 instanceof IntLiteral && v2 instanceof IntLiteral)
				return new IntLiteral(((IntLiteral)v1).value + ((IntLiteral)v2).value);
			return new StringLiteral(v1.asText() + v2.asText());
		} else {
			assert e_ instanceof SubtractExpression;
			if (v1 instanceof IntLiteral && v2 instanceof IntLiteral)
				return new IntLiteral(((IntLiteral)v1).value - ((IntLiteral)v2).value);
			throw new RuntimeException("Cannot subtract strings!");
		}
	}
	
	abstract Literal evaluate();
}

abstract class Literal extends Expression {
	abstract String asText();
	Literal evaluate() { return this; }
}

class IntLiteral extends Literal {
	int value;
	IntLiteral(int value) { this.value = value; }
	String asText() { return String.valueOf(value); }
}

class StringLiteral extends Literal {
	String value;
	StringLiteral(String value) { this.value = value; }
	String asText() { return value; }
}

abstract class BinaryOperatorExpression extends Expression {
	Expression e1, e2;
	Literal evaluate() {
		Literal v1 = e1.evaluate();
		Literal v2 = e2.evaluate();
		return compute(v1, v2);
	}
	abstract Literal compute(Literal v1, Literal v2);
}

class AddExpression extends BinaryOperatorExpression {
	AddExpression(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	Literal compute(Literal v1, Literal v2) {
		if (v1 instanceof IntLiteral && v2 instanceof IntLiteral)
			return new IntLiteral(((IntLiteral)v1).value + ((IntLiteral)v2).value);
		return new StringLiteral(v1.asText() + v2.asText());
	}
}

class SubtractExpression extends BinaryOperatorExpression {
	SubtractExpression(Expression e1, Expression e2) {
		this.e1 = e1;
		this.e2 = e2;
	}
	Literal compute(Literal v1, Literal v2) {
		if (v1 instanceof IntLiteral && v2 instanceof IntLiteral)
			return new IntLiteral(((IntLiteral)v1).value - ((IntLiteral)v2).value);
		throw new RuntimeException("Can't subtract strings!");
	}
}

class ExpressionsTest {

	@Test
	void test() {
		Expression e1 = new AddExpression(new StringLiteral("Hello"), new StringLiteral(" world"));
		Literal v1 = Expression.evaluate(e1);
		assertTrue(v1 instanceof StringLiteral);
		
		Expression e2 = new AddExpression(new IntLiteral(10), new IntLiteral(20));
		Expression e3 = new AddExpression(e2, new StringLiteral(" = 30?"));
		
		assertTrue(((StringLiteral)Expression.evaluate(e3)).value.equals("30 = 30?"));
	}

}
