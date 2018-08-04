package com.qiuyj.excel.config;

/**
 * @author qiuyj
 * @since 2018-08-04
 */
public class ExcelHeaderConfig {

  private String title; // 标题

  private int fontSize = 12; // 默认字体大小为12

  private String fontName = "";

  private int columnWidth;  // 列宽

  public ExcelHeaderConfig() {
  }

  public ExcelHeaderConfig(String title) {
    this.title = title;
  }

  public ExcelHeaderConfig(String title, int columnWidth) {
    this.title = title;
    this.columnWidth = columnWidth;
  }

  public ExcelHeaderConfig(String title, int columnWidth, int fontSize, String fontName) {
    this(title, columnWidth);
    this.fontSize = fontSize;
    this.fontName = fontName;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  public String getFontName() {
    return fontName;
  }

  public void setFontName(String fontName) {
    this.fontName = fontName;
  }

  public int getColumnWidth() {
    return columnWidth;
  }

  public void setColumnWidth(int columnWidth) {
    this.columnWidth = columnWidth;
  }
}
