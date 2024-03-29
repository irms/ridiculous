package org.cart.igd.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;

/**
 * BitmapLoader.java
 *
 * General Function: Load Bitmap Images.
 */
public class BitmapLoader
{
	/**
	 * loadBitmap
	 *
	 * General Function: Loads a specified Bitmap image as a BufferedImage.
	 *
	 * @param fn The file name of the image to be loaded.
	 */
	public static BufferedImage loadBitmap(String fn) throws IOException
	{
		BufferedImage image;
		InputStream input = null;
		try
		{
			input = ResourceRetriever.getResourceAsStream(fn);
			
			int bitmapFileHeaderLength = 14;
            int bitmapInfoHeaderLength = 40;

            byte bitmapFileHeader[] = new byte[bitmapFileHeaderLength];
            byte bitmapInfoHeader[] = new byte[bitmapInfoHeaderLength];

            input.read(bitmapFileHeader, 0, bitmapFileHeaderLength);
            input.read(bitmapInfoHeader, 0, bitmapInfoHeaderLength);

            bytesToInt(bitmapFileHeader, 2);
            int nWidth = bytesToInt(bitmapInfoHeader, 4);
            int nHeight = bytesToInt(bitmapInfoHeader, 8);
            bytesToInt(bitmapInfoHeader, 0);
            bytesToShort(bitmapInfoHeader, 12);
            int nBitCount = bytesToShort(bitmapInfoHeader, 14);
            int nSizeImage = bytesToInt(bitmapInfoHeader, 20);
            bytesToInt(bitmapInfoHeader, 16);
            int nColoursUsed = bytesToInt(bitmapInfoHeader, 32);
            bytesToInt(bitmapInfoHeader, 24);
            bytesToInt(bitmapInfoHeader, 28);
            bytesToInt(bitmapInfoHeader, 36);

            if(nBitCount==24)
                image = read24BitBitmap(nSizeImage, nHeight, nWidth, input);
            else if(nBitCount==8)
                image = read8BitBitmap(nColoursUsed, nBitCount, nSizeImage, nWidth, nHeight, input);
            else
            {
                System.out.println("Not a 24-bit or 8-bit Windows Bitmap, aborting...");
                image = null;
            }
        }
		finally
		{
            try { if(input!=null) input.close(); }
            catch (IOException e) {}
        }
        return image;
	}
	
	/**
	 * read8BitBitmap
	 *
	 * General Function: Reads an 8-bit Bitmap.
	 *
	 * @param nColoursUsed The number of colors used in the image.
	 * @param nBitCount The image's bit count.
	 * @param nSizeImage The size of the image.
	 * @param nWidth The width of the image.
	 * @param nHeight The height of the image.
	 * @param input The InputStream of the image.
	 */
	private static BufferedImage read8BitBitmap(int nColoursUsed, int nBitCount, int nSizeImage, int nWidth, int nHeight, InputStream input) throws IOException {
        int nNumColors = (nColoursUsed > 0) ? nColoursUsed : (1 & 0xff) << nBitCount;

        if (nSizeImage == 0) {
            nSizeImage = ((((nWidth * nBitCount) + 31) & ~31) >> 3);
            nSizeImage *= nHeight;
        }

        int npalette[] = new int[nNumColors];
        byte bpalette[] = new byte[nNumColors * 4];
        readBuffer(input, bpalette);
        int nindex8 = 0;

        for (int n = 0; n < nNumColors; n++) {
            npalette[n] = (255 & 0xff) << 24 |
                    (bpalette[nindex8 + 2] & 0xff) << 16 |
                    (bpalette[nindex8 + 1] & 0xff) << 8 |
                    (bpalette[nindex8 + 0] & 0xff);

            nindex8 += 4;
        }

        int npad8 = (nSizeImage / nHeight) - nWidth;
        BufferedImage bufferedImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
        DataBufferInt dataBufferByte = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer());
        int[][] bankData = dataBufferByte.getBankData();
        byte bdata[] = new byte[(nWidth + npad8) * nHeight];

        readBuffer(input, bdata);
        nindex8 = 0;

        for (int j8 = nHeight - 1; j8 >= 0; j8--) {
            for (int i8 = 0; i8 < nWidth; i8++) {
                bankData[0][j8 * nWidth + i8] = npalette[((int) bdata[nindex8] & 0xff)];
                nindex8++;
            }
            nindex8 += npad8;
        }

        return bufferedImage;
    }
    
    /**
     * read24BitBitmap
     *
     * General Function: Reads a 24-Bit Bitmap.
     *
	 * @param nSizeImage The size of the image.
	 * @param nWidth The width of the image.
	 * @param nHeight The height of the image.
	 * @param input The InputStream of the image.
     */
    private static BufferedImage read24BitBitmap(int nSizeImage, int nHeight, int nWidth, InputStream input) throws IOException
    {
        int npad = (nSizeImage / nHeight) - nWidth * 3;
        if(npad==4 || npad<0)
		{
			npad = 0;
		}
        int nindex = 0;
        BufferedImage bufferedImage = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_4BYTE_ABGR);
        DataBufferByte dataBufferByte = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer());
        byte[][] bankData = dataBufferByte.getBankData();
        byte brgb[] = new byte[(nWidth + npad) * 3 * nHeight];

        readBuffer(input, brgb);

        for(int j = nHeight - 1; j >= 0; j--)
        {
            for(int i = 0; i < nWidth; i++)
			{
                int base = (j * nWidth + i) * 4;
                bankData[0][base] = (byte) 255;
                bankData[0][base + 1] = brgb[nindex];
                bankData[0][base + 2] = brgb[nindex + 1];
                bankData[0][base + 3] = brgb[nindex + 2];
                nindex += 3;
            }
            nindex += npad;
        }

        return bufferedImage;
    }

	/**
	 * bytesToInt
	 *
	 * General Function: Converts a byte array into an intenger.
	 *
	 * @param bytes The byte array to convert to an integer.
	 * @param index The byte array offset to start from.
	 */
    private static int bytesToInt(byte[] bytes, int index)
    {
        return	(bytes[index + 3] & 0xff) << 24 |
				(bytes[index + 2] & 0xff) << 16 |
				(bytes[index + 1] & 0xff) << 8 |
				bytes[index + 0] & 0xff;
    }

	/**
	 * bytesToShort
	 *
	 * General Function: Converts a byte array into a short.
	 *
	 * @param bytes The byte array to convert to a short.
	 * @param index The byte array offset to start from.
	 */
    private static short bytesToShort(byte[] bytes, int index)
    {
        return (short) (((bytes[index + 1] & 0xff) << 8) | (bytes[index + 0] & 0xff));
    }

	/**
	 * readBuffer
	 *
	 * General Function: Reads a byte array buffer from an InputStream.
	 *
	 * @param in The InputStream to read the buffer from.
	 * @param buffer The byte array as a buffer.
	 */
	private static void readBuffer(InputStream in, byte[] buffer) throws IOException
	{
        int bytesRead = 0;
        int bytesToRead = buffer.length;
        while(bytesToRead > 0)
        {
            int read = in.read(buffer, bytesRead, bytesToRead);
            bytesRead += read;
            bytesToRead -= read;
        }
    }
}
