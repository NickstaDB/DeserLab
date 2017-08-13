# DeserLab
Java deserialization exploitation lab.

Simple Java client and server application that implements a custom network protocol using the Java serialization format to demonstrate Java deserialization vulnerabilities.

Download v1.0 built and ready to run from here: https://github.com/NickstaDB/DeserLab/releases/download/v1.0/DeserLab-v1.0.zip

## Usage
First launch the server-side component as follows:

    $ java -jar DeserLab.jar -server <listen-address> <listen-port>

Next, use the client to interact with the server component as follows:

    $ java -jar DeserLab.jar -client <server-address> <server-port>

Now pop some calcs ;)

**Note:** If you build DeserLab.jar yourself then you will need to make sure there is a library containing useful POP gadgets available on the CLASSPATH e.g.:

    $ java -cp <gadgetlib> -jar DeserLab.jar -server <listen-address> <listen-port>
