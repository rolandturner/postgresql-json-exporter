/*
JDBC to JSON exporter
Copyright (C) 2012 Roland Turner http://rolandturner.com/

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

Note that this program depends upon json-simple
https://code.google.com/p/json-simple/ which is distributed under the
Apache License, Version 2.0. If the interpretation of the Gnu GPL in your
jurisdiction means that you're not able to distribute this program or a
derivative of it because of the different license on json-simple then
additional permission is hereby granted to distribute this program and
derivatives of it anyway with all other aspects of the Gnu GPL in full
effect, and likewise to your licensees under the Gnu GPL, and theirs, etc.
*/

import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.simple.*;

public class Convert
	{
	public static void main(String[] args)
		throws Exception
		{
		List<String> dropped = new ArrayList<String>();

		Class.forName("org.postgresql.Driver");

		for (String connectionURL : args)
			{
			Connection c = DriverManager.getConnection(connectionURL);
			ResultSet rs = c.getMetaData().getTables(null, "public", null, new String[] { "TABLE" });

			while (rs.next())
				{
				String tableName = rs.getString("table_name");

				if (tableName.equals("version"))
					System.err.println("WARNING: skipping table named 'version' to avoid MongoDB conflict");
				else
					{
					if (!dropped.contains(tableName))
						{
						System.out.println("db." + tableName + ".drop()");
						dropped.add(tableName);
						}
	
					ResultSet rs2 = c.createStatement().executeQuery("SELECT * FROM " + tableName);
					ResultSetMetaData rsmd2 = rs2.getMetaData();
	
					while (rs2.next())
						{
						JSONObject o = new JSONObject();
						o.put("_connectionURL", connectionURL);
	
						for (int col = 1; col <= rsmd2.getColumnCount(); col++) // N.B. 1-based numbering for columns
							o.put(rsmd2.getColumnName(col), rs2.getString(col));
	
						System.out.println("db." + tableName + ".insert(" + o + ");");
						}
					}
				}
			}
		}
	}
