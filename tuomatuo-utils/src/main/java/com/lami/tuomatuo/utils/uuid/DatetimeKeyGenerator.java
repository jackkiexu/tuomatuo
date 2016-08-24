/**
 * 2012-11-9 - 15:35:55
 */
package com.lami.tuomatuo.utils.uuid;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 日期、时间算法的主键生成器.
 * 
 * @author bo.wen
 */
public class DatetimeKeyGenerator implements IDGenerator{
	
	/** The counter. */
	private static AtomicLong counter = new AtomicLong();
	
	DatetimeKeyGenerator()
	{
	}

	/* (non-Javadoc)
	 * @see com.actionsky.rds.ha.util.IDGenerator#generate()
	 */
	public CharSequence generate() {
		Calendar calendar = new GregorianCalendar();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int minutes = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int randCount = ThreadLocalRandom.current().nextInt(1000000);
		return new StringBuilder().append(year)
								  .append(month)
								  .append(day)
								  .append(hour)
								  .append(minutes)
								  .append(second)
								  .append(randCount)
								  .append(getJVMCount());
	}

	/**
	 * Gets the jVM count.
	 * 
	 * @return the jVM count
	 */
	private static long getJVMCount() {
        return counter.incrementAndGet();
	}
}
