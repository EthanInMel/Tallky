package ChatroomServer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class HelperMethods {
	public static void sendMessage(SocketChannel socketChannel, String message) {
		try {
			Charset c = Charset.forName("UTF-8");			
			ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(c));
			buffer.put(message.getBytes());
			buffer.flip();
			while (buffer.hasRemaining()) {
				socketChannel.write(buffer);
			}
			System.out.print("Sent: " + message);
		} catch (IOException ex) {
//			ex.printStackTrace();
		}
	}

	public static String receiveMessage(SocketChannel socketChannel) {
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(64);
			int counter = socketChannel.read(byteBuffer);
			while (true) {
				if(counter == -1)
					return "";
				byteBuffer.flip();	
				if((char)byteBuffer.get(byteBuffer.limit()-1) != '\n')
				{
					ByteBuffer tempBuffer = byteBuffer;
					byteBuffer = ByteBuffer.allocate(byteBuffer.capacity()*2);
					byteBuffer.put(tempBuffer);		
				}
				else
					break;
				counter = socketChannel.read(byteBuffer);
			}
			
			Charset c = Charset.forName("UTF-8");
			CharsetDecoder cd = c.newDecoder();
			CharBuffer cb = cd.decode(byteBuffer);
			System.out.print("get: " + cb.toString());
			return cb.toString();
		} catch (IOException ex) {

		}
		return "";
	}
	
	public static boolean IsNameLegal(String identity) {
		if(!Character.isLetter(identity.charAt(0)) || identity.matches(".*[^a-zA-Z0-9]+.*") || identity.length() > 16 || identity.length() < 3)
		{
			return false;
		}
	    return true;
	   }
}
