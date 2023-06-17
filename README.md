# Skunk exception printing reproducer

https://github.com/typelevel/skunk/issues/905

Running this project requires Docker

```shell
> sbt run
[info] running skunk_logs.Main 
java.lang.RuntimeException: This error will be handled and shouldn't be printed
	at skunk_logs.Main$.dangerousQuery$$anonfun$1(Main.scala:41)
	at as @ fs2.io.net.SocketGroupCompanionPlatform$AsyncSocketGroup.connect$1$$anonfun$1$$anonfun$1(SocketGroupPlatform.scala:76)
	at flatMap @ skunk.net.BufferedMessageSocket$$anon$1.receive$$anonfun$1(BufferedMessageSocket.scala:153)
	at get @ skunk.util.Pool$.free$1(Pool.scala:156)
	at flatMap @ skunk.net.BufferedMessageSocket$$anon$1.receive(BufferedMessageSocket.scala:154)
Handling the query error, so it definitely shouldn't be printed
```