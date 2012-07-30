Convert.class : Convert.java
	javac -classpath /usr/share/java/json_simple.jar $<

run : Convert.class
	java -classpath .:/usr/share/java/postgresql.jar:/usr/share/java/json_simple.jar Convert jdbc:postgresql:trac-Personal jdbc:postgresql:trac-TrustSphere >import.js
	/usr/lib/meteor/mongodb/bin/mongo localhost:3002/meteor import.js
