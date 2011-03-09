/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>, Artjom Kochtchi
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.base;

import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Working with money (amount + currency).
 */
public final class Money implements Comparable<Money> {

	public static void main(String[] args) {
		System.out.println(new Money(12000000000d, USD).toString(Locale.GERMANY));
	}

	public static final transient String EUR = "EUR";
	public static final transient String USD = "USD";

	private long cent;
	private String currency;

	public Money(long value, long cent, String currency) {
		this.cent = (value * 100) + cent;
		this.currency = currency;
		if (this.currency == null) throw new RuntimeException("currency == null");
	}

	public Money(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s, " ");
		if (!tokenizer.hasMoreTokens()) throw new RuntimeException("Illegal money format: " + s);
		String amount = tokenizer.nextToken();
		this.cent = Math.round(Double.parseDouble(amount.replace(',', '.')) * 100);
		if (!tokenizer.hasMoreTokens()) throw new RuntimeException("Illegal money format (missing currency): " + s);
		this.currency = tokenizer.nextToken();
		if (this.currency == null) throw new RuntimeException("currency == null");
	}

	public Money(String amount, String currency) {
		this.cent = Math.round(Double.parseDouble(amount.replace(',', '.')) * 100);
		this.currency = currency;
		if (this.currency == null) throw new RuntimeException("currency == null");
	}

	public Money(double value, String currency) {
		this.cent = Math.round(value * 100);
		this.currency = currency;
		if (this.currency == null) throw new RuntimeException("currency == null");
	}

	public String getCurrency() {
		return currency;
	}

	public float getAmountAsFloat() {
		return cent / 100f;
	}

	public double getAmountAsDouble() {
		return cent / 100d;
	}

	public long getAmountAsCent() {
		return cent;
	}

	public boolean isPositive() {
		return cent >= 0;
	}

	public boolean isNegative() {
		return cent < 0;
	}

	public Money negate() {
		return new Money(0, cent * -1, currency);
	}

	public Money substract(Money money) {
		if (money == null) return this;
		if (!currency.equals(money.currency))
			throw new RuntimeException("Cannot subtract " + money.currency + " from " + currency + ".");
		return new Money(0, cent - money.cent, currency);
	}

	public Money add(Money money) {
		if (money == null) return this;
		if (!currency.equals(money.currency))
			throw new RuntimeException("Cannot add " + money.currency + " to " + currency + ".");
		return new Money(0, cent + money.cent, currency);
	}

	public Money invert() {
		return new Money(0, cent * -1, currency);
	}

	public Money multiply(float factor) {
		return new Money(0, Math.round((cent * factor)), currency);
	}

	public Money divide(float divisor) {
		return new Money(0, Math.round((cent / divisor)), currency);
	}

	public String getAmountAsString(char decimalSeparator) {
		return getAmountAsString(decimalSeparator, null);
	}

	public String getAmountAsString(char decimalSeparator, String thousandsSeparator) {
		boolean neg = false;
		long c = cent;
		if (c < 0) {
			neg = true;
			c = Math.abs(c);
		}
		long value = c / 100;
		long rest = c - (value * 100);
		StringBuilder sb = new StringBuilder();
		if (neg) sb.append("-");
		sb.append(Str.formatWithThousandsSeparator(value, thousandsSeparator));
		sb.append(decimalSeparator);
		if (rest < 10) sb.append('0');
		sb.append(rest);
		return sb.toString();
	}

	public String toString(Locale locale) {
		if (locale != null) {
			if (locale.equals(Locale.GERMAN) || locale.equals(Locale.GERMANY))
				return getAmountAsString(',', ".") + ' ' + currency;
		}
		return toString('.');
	}

	public String toString(char decimalSeparator) {
		return getAmountAsString(decimalSeparator) + ' ' + currency;
	}

	public boolean isLessThen(Money m) {
		return cent < m.cent;
	}

	public boolean isLessThenOrEqualTo(Money m) {
		return cent <= m.cent;
	}

	public boolean isGreaterThen(Money m) {
		return cent > m.cent;
	}

	public boolean isGreaterThenOrEqualTo(Money m) {
		return cent >= m.cent;
	}

	@Override
	public String toString() {
		return toString(null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		Money other = (Money) obj;
		return cent == other.cent && currency.equals(other.currency);
	}

	private int hashCode;

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = 23;
			hashCode = hashCode * 37 + (int) cent;
			hashCode = hashCode * 37 + currency.hashCode();
		}
		return hashCode;
	}

	public int compareTo(Money o) {
		if (cent > o.cent) return 1;
		if (cent < o.cent) return -1;
		return 0;
	}

	// --- ---

	public static Money computeAvg(Money[] moneys, String currency) {
		return computeSum(moneys, currency).divide(moneys.length);
	}

	public static Money computeSum(Money[] moneys, String currency) {
		Money sum = new Money(0, currency);
		for (Money money : moneys) {
			sum = sum.add(money);
		}
		return sum;
	}

	public static Money[] createArray(int size, Money initialValue) {
		Money[] moneys = new Money[size];
		for (int i = 0; i < size; i++)
			moneys[i] = initialValue;
		return moneys;
	}

}
