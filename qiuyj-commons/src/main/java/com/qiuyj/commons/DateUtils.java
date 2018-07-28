package com.qiuyj.commons;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

/**
 * @author qiuyj
 * @since 2018/3/4
 */
public abstract class DateUtils {

  /**
   * 得到给定年份的第一天的LocalDate对象
   * @param year 年份
   * @return LocalDate对象
   */
  public static LocalDate getFirstLocalDateOfYear(int year) {
    return LocalDate.of(year, 1, 1);
  }

  /**
   * 得到给定年份的第一天的Date对象
   * @param year 年份
   * @return Date对象
   */
  public static Date getFirstDateOfYear(int year) {
    Calendar c = Calendar.getInstance();
    c.clear();
    c.set(Calendar.YEAR, year);
    return c.getTime();
  }

  /**
   * 得到给定年份的最后一天的LocalDate对象
   * @param year 年份
   * @return LocalDate对象
   */
  public static LocalDate getLastLocalDateOfYear(int year) {
    return LocalDate.of(year, 12, 31);
  }

  /**
   * 得到给定年份的最后一天的Date对象
   * @param year 年份
   * @return Date对象
   */
  public static Date getLastDateOfYear(int year) {
    Calendar c = Calendar.getInstance();
    c.clear();
    c.set(Calendar.YEAR, year);
    c.roll(Calendar.DAY_OF_YEAR, -1);
    return c.getTime();
  }

  /**
   * 得到给定日期是当前月份的第几周，每个月1号到7号是第一周，8号到14号是第二周
   * 15号到21是第三周，22号到28号是第四周等等
   * @param yyyy_MM_dd 给定的日期字符串表达式
   * @return 当前日期是当前月份的周数
   */
  public static int getWeekOfMonth(String yyyy_MM_dd) {
    DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE;
    LocalDate parsedDate = dtf.parse(yyyy_MM_dd, LocalDate::from);
    int dayOfMonth = parsedDate.getDayOfMonth();
    return dayOfMonth % 7 == 0 ? dayOfMonth / 7 : dayOfMonth / 7 + 1;
  }

  /**
   * 得到某个月份给定周数的开始和结束日期
   * @param year 年份
   * @param month 月份
   * @param week 周数
   * @return 开始和结束日期组成的Map，{"start": LocalDate, "end": LocalDate}
   */
  public static Map<String, LocalDate> getStartAndEndLocalDateOfWeekInMonth(int year, int month, int week) {
    YEAR.checkValidValue(year);
    MONTH_OF_YEAR.checkValidValue(month);
    int dayNumber = getMonthLength(year, month),
        end = week * 7,
        start = end - 6;
    if (end > dayNumber) {
      start = 29;
      end = dayNumber;
    }
    Map<String, LocalDate> rs = new HashMap<>(8);
    rs.put("start", LocalDate.of(year, month, start));
    rs.put("end", LocalDate.of(year, month, end));
    return rs;
  }

  /**
   * 得到某一年的某个月的天数
   * @param year 年份
   * @param month 月份
   * @return 天数
   */
  public static int getMonthLength(int year, int month) {
    int day = 31;
    switch (month) {
      case 2:
        day = (IsoChronology.INSTANCE.isLeapYear(year) ? 29 : 28);
        break;
      case 4:
      case 6:
      case 9:
      case 11:
        day = 30;
        break;
    }
    return day;
  }

  /**
   * 生成类似yyyyMMddHHmmssSSS的字符串
   */
  public static String getDateTimeString() {
    LocalDateTime now = LocalDateTime.now();
    StringBuilder sb = new StringBuilder(17);
    int value = now.getYear();
    sb.append(value);
    value = now.getMonthValue();
    if (value < 10) {
      sb.append("0");
    }
    sb.append(value);
    value = now.getDayOfMonth();
    if (value < 10) {
      sb.append("0");
    }
    sb.append(value);
    value = now.getHour();
    if (value < 10) {
      sb.append("0");
    }
    sb.append(value);
    value = now.getMinute();
    if (value < 10) {
      sb.append("0");
    }
    sb.append(value);
    value = now.getSecond();
    if (value < 10) {
      sb.append("0");
    }
    sb.append(value);
    value = now.get(MILLI_OF_SECOND);
    if (value < 10 && value >= 0) {
      sb.append("00");
    }
    else if (value < 100 && value >= 10) {
      sb.append("0");
    }
    sb.append(value);
    return sb.toString();
  }

  /**
   * 根据给定的时间日期，判断是否是周末（包括星期六和星期日）
   * @param dateTime 时间日期
   * @return 如果是周末，返回{@code true}，否则返回{@code false}
   */
  public static boolean isWeekend(LocalDateTime dateTime) {
    return isWeekend(dateTime.toLocalDate());
  }

  /**
   * 根据给定的时间日期，判断是否是周末（包括星期六和星期日）
   * @param date 时间日期
   * @return 如果是周末，返回{@code true}，否则返回{@code false}
   */
  public static boolean isWeekend(LocalDate date) {
    DayOfWeek dow = date.getDayOfWeek();
    return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
  }

  /**
   * 根据给定的时间日期，判断是否是周末（包括星期六和星期日）
   * @param date 时间日期
   * @return 如果是周末，返回{@code true}，否则返回{@code false}
   */
  public static boolean isWeekend(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    int dow = c.get(Calendar.DAY_OF_WEEK);
    return dow == Calendar.SATURDAY || dow == Calendar.SUNDAY;
  }

  /**
   * 得到一个月的工作天数（不包括周六和周日的天数）
   * @param yearMonth 月份所在年的对象
   * @apiNote 没有考虑国家法定节假日（如果需要考虑国家法定节假日，一定不能使用该方法）
   * @return 一个月的实际工作天数（不包括周六和周日的天数）
   */
  public static int getWorkingDaysOfMonth(YearMonth yearMonth) {
    LocalDate date = yearMonth.atDay(1);
    int days = yearMonth.lengthOfMonth(), unchangeDays = days,
        weekDay = date.getDayOfWeek().getValue(),
        workingDays = 0;
    if (weekDay == 6) {
      days -= 2;
    }
    else if (weekDay == 7) {
      days -= 1;
    }
    else {
      workingDays += (6 - weekDay);
      days -= weekDay;
    }
    do {
      workingDays += 5;
      days -= 7;
    }
    while (days > 7);
    if (days > 0) {
      // 得到这个月的最后一天的LocalDate对象
      date = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), unchangeDays);
      weekDay = date.getDayOfWeek().getValue();
      if (weekDay == 7) {
        days -= 1;
      }
      else if (weekDay == 6) {
        days -= 2;
      }
      if (days > 0) {
        workingDays += days;
      }
    }
    return workingDays;
  }

  /**
   * 得到一个月的工作天数
   * @param year 年
   * @param month 月
   * @return 工作天数
   */
  public static int getWorkingDaysOfMonth(int year, int month) {
    return getWorkingDaysOfMonth(YearMonth.of(year, month));
  }

}