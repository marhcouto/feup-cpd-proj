How to compile:
    Change work directory with "cd assign2/src/src"
    Double check if Java JDK >= 17 is in the path
    Run "javac store/Store.java" to compile store server
    Run "javac client/TestClient.java" to compile test client

How to run:
    Change work directory with "cd assign2/src/src"
    To run TestClient "java client.TestClient"
    To run the store "java store.Store"
    The arguments are the same as specified in the handout. Example:
    STORE: java store.Store 224.0.0.1 3030 127.0.0.1 3000
    CLIENT: java client.TestClient 127.0.0.1:3000 get <file_key> or
    	    java client.TestClient 127.0.0.1:MembershipService join

Some notes on RMI:
    The store already opens rmiregistry
    The name of the membership service is "MembershipService"
