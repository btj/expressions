package expressions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

abstract class Expression {
	// Uitrekenen van een uitdrukking, zonder dynamische binding
	static Literal evaluate(Expression e) {
		if (e instanceof Literal)
			return (Literal)e;
		BinaryOperatorExpression e_ = (BinaryOperatorExpression)e;
		Literal v1 = evaluate(e_.e1);
		Literal v2 = evaluate(e_.e2);
		if (e_ instanceof AddExpression) {
			if (v1 instanceof IntLiteral && v2 instanceof IntLiteral)
				return new IntLiteral(((IntLiteral)v1).value + ((IntLiteral)v2).value);
			return new StringLiteral(Literal.asText(v1) + Literal.asText(v2));
		} else {
			assert e_ instanceof SubtractExpression;
			if (v1 instanceof IntLiteral && v2 instanceof IntLiteral)
				return new IntLiteral(((IntLiteral)v1).value - ((IntLiteral)v2).value);
			throw new RuntimeException("Cannot subtract strings!");
		}
	}
	
	// Uitrekenen van een uitdrukking, met dynamische binding
	abstract Literal evaluate();
}

abstract class Literal extends Expression {
	// Omzetting van een letterlijke uitdrukking naar een tekenreeks, zonder dynamische binding
	static String asText(Literal literal) {
		if (literal instanceof IntLiteral) {
			return String.valueOf(((IntLiteral)literal).value);
		} else {
			return ((StringLiteral)literal).value;
		}
	}
	
	abstract String asText();
	public String toString() { return asText(); }
	Literal evaluate() { return this; }
}

class IntLiteral extends Literal {
	int value;
	IntLiteral(int value) { this.value = value; }
	String asText() { return String.valueOf(value); }
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IntLiteral))
			return false;
		return value == ((IntLiteral)other).value;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}
}

class StringLiteral extends Literal {
	String value;
	StringLiteral(String value) { this.value = value; }
	String asText() { return value; }
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof StringLiteral))
			return false;
		return value.equals(((StringLiteral)other).value);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}
}

abstract class BinaryOperatorExpression extends Expression {
	public Expression e1, e2;
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
		// object e1 stelt de uitdrukking "Hello" + " world" voor
		Expression e1 = new AddExpression(new StringLiteral("Hello"), new StringLiteral(" world"));
		assertEquals(new StringLiteral("Hello world"), Expression.evaluate(e1));
		assertEquals(new StringLiteral("Hello world"), e1.evaluate());
		assertNotEquals(new IntLiteral(42), new StringLiteral("Hello world")); // Gedraagt de equals-methode van klasse StringLiteral zich correct?
		
		assertEquals("Hello world", "" + new StringLiteral("Hello world")); // Test van toString()
		
		// object e3 stelt de uitdrukking (10 + 20) + " = 30?" voor
		Expression e2 = new AddExpression(new IntLiteral(10), new IntLiteral(20));
		Expression e3 = new AddExpression(e2, new StringLiteral(" = 30?"));
		assertEquals(new StringLiteral("30 = 30?"), Expression.evaluate(e3));
		assertEquals(new StringLiteral("30 = 30?"), e3.evaluate());
		
		// object e4 stelt de uitdrukking (10 + 20) - 5 voor
		Expression e4 = new SubtractExpression(e2, new IntLiteral(5));
		assertEquals(new IntLiteral(25), Expression.evaluate(e4));
		assertEquals(new IntLiteral(25), e4.evaluate());
		assertNotEquals(new StringLiteral("Hello"), new IntLiteral(25)); // Gedraagt de equals-methode van klasse IntLiteral zich correct? 
		
		assertThrows(RuntimeException.class, () ->
			new SubtractExpression(new StringLiteral("Hello"), new StringLiteral("world")).evaluate());
		assertThrows(RuntimeException.class, () ->
			Expression.evaluate(new SubtractExpression(new StringLiteral("Hello"), new StringLiteral("world"))));
		
		// Gelijke objecten moeten gelijke hash codes hebben.
		assertEquals(new IntLiteral(25).hashCode(), new IntLiteral(25).hashCode());
		assertEquals(new StringLiteral("Hello").hashCode(), new StringLiteral("Hello").hashCode());
	}

}
