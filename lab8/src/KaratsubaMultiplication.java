import java.math.BigInteger;

import java.util.Random;

//cannot use BigInteger predefined methods for multiplication
//cannot use Strings except in computing appropriate exponent
public class KaratsubaMultiplication
{
	private static final BigInteger MAX_INT_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

	public static BigInteger karatsuba(final BigInteger factor0, final BigInteger factor1, final int base)
	{
		//downshift to regular multiplication if the factors are both less than the maximum integer values to create a long value
        int factor0_bl = factor0.bitLength();
        int factor1_bl = factor1.bitLength();
        boolean factor0_lt_max_int = factor0_bl <= MAX_INT_VALUE.bitLength();
        boolean factor1_lt_max_int = factor1_bl <= MAX_INT_VALUE.bitLength();
        if(factor0_lt_max_int && factor1_lt_max_int) {
            return BigInteger.valueOf(factor0.longValue() * factor1.longValue());
        }

    	//we want to divide the number of digits in half (based on the base representation)
        int shift_bits = (factor0_bl > factor1_bl) ? factor0_bl >> 1 : factor1_bl >> 1;
        BigInteger f0_upper_bits = factor0.shiftRight(shift_bits);
        BigInteger f0_lower_bits = factor0.subtract(f0_upper_bits.shiftLeft(shift_bits));
        BigInteger f1_upper_bits = factor1.shiftRight(shift_bits);
        BigInteger f1_lower_bits = factor1.subtract(f1_upper_bits.shiftLeft(shift_bits));

    	//algorithm
        BigInteger T_0 = karatsuba(f0_lower_bits, f1_lower_bits, base);
        BigInteger T_1 = karatsuba(f0_lower_bits.add(f0_upper_bits), f1_lower_bits.add(f1_upper_bits), base);
        BigInteger T_2 = karatsuba(f0_upper_bits, f1_upper_bits, base);
        BigInteger result =
            T_2.shiftLeft(shift_bits * 2)
                .add(T_1
                    .subtract(T_0)
                    .subtract(T_2)
                    .shiftLeft(shift_bits))
                .add(T_0);
        return result;
	}

	public static void main(String[] args)
	{
		//test cases
		if(args.length < 3)
		{
			System.out.println("Need two factors and a base value as input");
			return;
		}
		BigInteger factor0 = null;
		BigInteger factor1 = null;
		final Random r = new Random();
		if(args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("rand") || args[0].equalsIgnoreCase("random"))
		{
			factor0 = new BigInteger(r.nextInt(100000), r);
			System.out.println("First factor : " + factor0.toString());
		}
		else
		{
            if(args[2].equals("2")) {
                char[] chars = args[0].toCharArray();
                factor0 = BigInteger.valueOf(0);
                for(int i = 0; i < chars.length; ++i) {
                    if(chars[i] == '1') {
                        factor0 = factor0.setBit(chars.length - i - 1);
                    }
                }
            } else {
    			factor0 = new BigInteger(args[0]);
            }
		}
		if(args[1].equalsIgnoreCase("r") || args[1].equalsIgnoreCase("rand") || args[1].equalsIgnoreCase("random"))
		{
			factor1 = new BigInteger(r.nextInt(100000), r);
			System.out.println("Second factor : " + factor1.toString());
		}
		else
		{
            if(args[2].equals("2")) {
                char[] chars = args[1].toCharArray();
                factor1 = BigInteger.valueOf(0);
                for(int i = 0; i < chars.length; ++i) {
                    if(chars[i] == '1') {
                        factor1 = factor0.setBit(chars.length - i - 1);
                    }
                }
            } else {
    			factor1 = new BigInteger(args[1]);
            }
		}
		final BigInteger result = karatsuba(factor0, factor1, Integer.parseInt(args[2]));
        if(args[2].equals("2")) {
            StringBuilder sresult = new StringBuilder();
            for(int i = result.bitLength() - 1; i >= 0; --i) {
                sresult.append(result.testBit(i) ? "1" : "0");
            }
    		System.out.println(sresult);
        } else {
    		System.out.println(result);
        }
		System.out.println(result.equals(factor0.multiply(factor1)));
	}
}
