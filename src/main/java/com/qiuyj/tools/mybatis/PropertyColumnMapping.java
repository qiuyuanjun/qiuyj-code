package com.qiuyj.tools.mybatis;

/**
 * @author qiuyj
 * @since 2017/11/15
 */
public class PropertyColumnMapping {
  private String javaClassPropertyName;
  private String databaseColumnName;

  public PropertyColumnMapping() {}

  public PropertyColumnMapping(String javaClassPropertyName, String databaseColumnName) {
    this.javaClassPropertyName = javaClassPropertyName;
    this.databaseColumnName = databaseColumnName;
  }

  public String getJavaClassPropertyName() {
    return javaClassPropertyName;
  }

  public void setJavaClassPropertyName(String javaClassPropertyName) {
    this.javaClassPropertyName = javaClassPropertyName;
  }

  public String getDatabaseColumnName() {
    return databaseColumnName;
  }

  public void setDatabaseColumnName(String databaseColumnName) {
    this.databaseColumnName = databaseColumnName;
  }

  @Override
  public String toString() {
    return databaseColumnName + " AS " + javaClassPropertyName;
  }
}