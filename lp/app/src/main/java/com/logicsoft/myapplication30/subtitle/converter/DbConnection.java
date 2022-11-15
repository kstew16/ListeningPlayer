//package kr.logicsoft.listeningplayer.subtitle.converter;
package com.logicsoft.myapplication30.subtitle.converter;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DbConnection {
	private static final String domain = "localhost";
	private static final int port = 1433;
	private static final String databaseName = "english";
	private static final String user = "sa";
	private static final String password = "hello1";
	private static final String MSSQL_JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	// private static final String connectionString = String.format(
	//		"jdbc:sqlserver://%s:%d;databaseName=%s;user=%s;password=%s", domain, port, databaseName, user, password);
	private static final String connectionString="jdbc:sqlServer://210.125.183.216;"+ "databaseName=english;" + "user=sa;password=hello1";

	public Connection connection = null;
	public Statement statement = null;

	public DbConnection() {
		openConnection();
	}

	@Override
	protected void finalize() throws Throwable {
		closeConnection();
		super.finalize();
	}

	private void openConnection() {
		try {
			Log.d("db", "before class for name");
			Class.forName(MSSQL_JDBC_DRIVER);
			Log.d("db", "after class for name: " + connectionString);
			this.connection = DriverManager.getConnection(connectionString);
			Log.d("db", "after connection");
//			this.statement = this.connection.createStatement();
		} catch (ClassNotFoundException e) {
			System.err.println("클래스 " + MSSQL_JDBC_DRIVER + "를 찾을 수 없습니다.");
			Log.d("db", "클래스 " + MSSQL_JDBC_DRIVER + "를 찾을 수 없습니다.");
		}   catch (SQLException e) {
			System.err.println("데이터베이스에 접근할 수 없습니다.");
			Log.d("db", "데이터베이스에 접근할 수 없습니다.");

		}
		System.out.println("데이터베이스 연결 " + connection + " 열림");
		Log.d("db", "데이터베이스 연결 " + connection + " 열림");
	}

	public boolean isConnected() {
		return this.connection != null;
	}

	public void closeConnection() {
		if (isConnected()) {
			String c = this.connection.toString();
			try {
				this.statement.close();
				this.connection.close();
				this.connection = null;
			} catch (SQLException e) {
				System.err.println(e);
			}
			System.out.println("데이터베이스 연결 " + c + " 닫힘");
		}
	}
}
