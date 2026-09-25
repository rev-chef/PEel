package file;

import java.io.*;
import java.security.*;
import java.util.ArrayList;

public class HashFile {
	
	/**
	 * Uses Java's MessageDigest class to generate all default file hashes for the input executable
	 * @param f
	 * @return ArrayList
	 */
	public static ArrayList<String> Hash(File f) {
		 String[] hashes = {"MD5", "SHA-1", "SHA-256", "SHA-512"};
		 ArrayList<String> fileHashes = new ArrayList<>();
		 
		try 
		{
			InputStream fileInput = new FileInputStream(f);
			StringBuilder hexString = new StringBuilder();
			for(String h : hashes)
			{
				
			
			MessageDigest digest = MessageDigest.getInstance(h);
			
			
			byte[] buffer = new byte[8192];
			int bytesRead;
			while((bytesRead = fileInput.read(buffer)) != -1)
			{
				digest.update(buffer, 0, bytesRead);
			}
			
			byte[] hash = digest.digest();
			
			
			for(byte b : hash) 
			{
				hexString.append(String.format("%02x", b));
			}
			fileHashes.add(hexString.toString());
			hexString.setLength(0);
			
		} 
			fileInput.close();
			return fileHashes;
	}
		
		
		
		catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
