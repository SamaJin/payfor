package com.helphand.ccdms.commons;

import java.io.*;

public class common_media_converts {
	private byte out_val;
	private FileOutputStream filewriter = null;
	private FileInputStream fileReader = null;

	public common_media_converts(String in_file_path, String out_file_path, int vox_format, int vox_rate, int bit_rate) {
		try {
			if (out_file_path == null) {
				out_file_path = in_file_path.substring(0, in_file_path.length() - 3);
				out_file_path = out_file_path + "wav";
			}
			File outF = new File(out_file_path);
			File inF = new File(in_file_path);
			long inFileSize = inF.length();
			if (vox_format == 1) {
				inFileSize *= 2L;
			}
			filewriter = new FileOutputStream(outF, false);
			String wavBegin = "RIFF";
			filewriter.write(wavBegin.getBytes());
			long wavLength = inFileSize * bit_rate + 36L;
			byte[] tmpArr = new byte[4];
			longToIntBinary(wavLength, tmpArr, 0);
			filewriter.write(tmpArr);
			String wavTag = "WAVEfmt ";
			filewriter.write(wavTag.getBytes());
			int headLength = 16;
			tmpArr = new byte[4];
			longToIntBinary(headLength, tmpArr, 0);
			filewriter.write(tmpArr);
			int wFormatTag = 1;
			tmpArr = new byte[2];
			toShortBinary(wFormatTag, tmpArr, 0);
			filewriter.write(tmpArr);
			int nChannels = 1;
			tmpArr = new byte[2];
			toShortBinary(nChannels, tmpArr, 0);
			filewriter.write(tmpArr);
			int nSamplesPerSec = vox_rate;
			tmpArr = new byte[4];
			longToIntBinary(nSamplesPerSec, tmpArr, 0);
			filewriter.write(tmpArr);
			int nAvgBytesPerSec = vox_rate * bit_rate;
			tmpArr = new byte[4];
			longToIntBinary(nAvgBytesPerSec, tmpArr, 0);
			filewriter.write(tmpArr);
			int nBlockAlign = bit_rate;
			tmpArr = new byte[2];
			toShortBinary(nBlockAlign, tmpArr, 0);
			filewriter.write(tmpArr);
			int wBitsPerSample = 8 * bit_rate;
			tmpArr = new byte[2];
			toShortBinary(wBitsPerSample, tmpArr, 0);
			filewriter.write(tmpArr);
			String dataTag = "data";
			filewriter.write(dataTag.getBytes());
			long Datalen = inFileSize * bit_rate;
			tmpArr = new byte[4];
			longToIntBinary(Datalen, tmpArr, 0);
			filewriter.write(tmpArr);
			fileReader = new FileInputStream(inF);
			byte[] outbytebuffer = new byte[20000];
			int[] outintbuffer = new int[20000];
			switch (vox_format) {
			case 1:
				short Sn = 0;
				int SS = 16;
				int SSindex = 1;
				int i = 0;
				byte[] b = new byte[10000];
				int allBytes = fileReader.available();
				while (true) {
					int thisRead = allBytes > 10000 ? 10000 : allBytes;
					int bytes = fileReader.read(b, 0, thisRead);

					allBytes -= thisRead;
					int outindex = 0;
					for (int index = 0; index < bytes; index++) {
						byte sample = b[index];
						byte highByte = (byte) (sample >>> 4 & 0xFF);
						byte lowByte = (byte) (sample & 0xF);

						if ((highByte == 0) || (highByte == 8)) {
							i++;
						}
						Object[] retVal = decode((byte) (sample >>> 4), Sn, SS, SSindex);
						Sn = ((Short) retVal[0]).shortValue();
						SS = ((Integer) retVal[1]).intValue();
						SSindex = ((Integer) retVal[2]).intValue();
						int out_int;
						if (bit_rate == 1) {
							out_int = Sn / 16;
							if (out_int > 127) {
								out_int = 127;
							}
							if (out_int < -128) {
								out_int = -128;
							}
							this.out_val = (byte) (out_int - 128 & 0xFF);

							outbytebuffer[outindex] = this.out_val;
						} else {
							out_int = Sn * 16;
							outintbuffer[outindex] = out_int;
						}
						outindex++;
						if (i == 48) {
							Sn = 0;
							SS = 16;
							i = 0;
						}
						if ((lowByte == 0) || (lowByte == 8)) {
							i++;
						}
						retVal = decode((byte) (sample & 0xF), Sn, SS, SSindex);
						Sn = ((Short) retVal[0]).shortValue();
						SS = ((Integer) retVal[1]).intValue();
						SSindex = ((Integer) retVal[2]).intValue();
						if (bit_rate == 1) {
							out_int = Sn / 16;
							if (out_int > 127) {
								out_int = 127;
							}
							if (out_int < -128) {
								out_int = -128;
							}
							this.out_val = (byte) (out_int - 128 & 0xFF);

							outbytebuffer[outindex] = this.out_val;
						} else {
							out_int = Sn * 16;
							outintbuffer[outindex] = out_int;
						}
						outindex++;
						if (i == 48) {
							Sn = 0;
							SS = 16;
							i = 0;
						}
					}
					if (bit_rate == 1) {
						filewriter.write(outbytebuffer);
					} else for (int j = 0; j < outintbuffer.length; j++) {
						byte[] arr = new byte[4];
						longToIntBinary(outintbuffer[j], arr, 0);
						filewriter.write(arr);
					}
					if (allBytes <= 0) {
						break;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				fileReader.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				filewriter.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	private Object[] decode(byte sample, short Sn, int SS, int SSindex) {
		Object[] retArr = new Object[3];
		int[] SSadjust = { -1, -1, -1, -1, 2, 4, 6, 8 };
		int[] SStable = { 0, 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190, 209, 230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552 };
		int Mn = 0;
		if ((sample & 0x4) != 0) {
			Mn = SS;
		}
		if ((sample & 0x2) != 0) {
			Mn += (SS >>> 1);
		}
		if ((sample & 0x1) != 0) {
			Mn += (SS >>> 2);
		}
		Mn += (SS >>> 3);
		if ((sample & 0x8) != 0) {
			Sn = (short) (Sn - Mn & 0xFFFF);
		} else {
			Sn = (short) (Sn + Mn & 0xFFFF);
		}
		if (Sn > 2047) {
			Sn = 2047;
		}
		if (Sn < -2048) {
			Sn = -2048;
		}
		SSindex += SSadjust[(sample & 0x7)];
		if (SSindex < 1) {
			SSindex = 1;
		}
		if (SSindex > 49) {
			SSindex = 49;
		}
		SS = SStable[SSindex];
		retArr[0] = Short.valueOf(Sn);
		retArr[1] = Integer.valueOf(SS);
		retArr[2] = Integer.valueOf(SSindex);
		return retArr;
	}

	private void longToIntBinary(long val, byte[] array, int offset) {
		array[offset] = (byte) (int) (val & 0xFF);
		array[(offset + 1)] = (byte) (int) (val >>> 8 & 0xFF);
		array[(offset + 2)] = (byte) (int) (val >>> 16 & 0xFF);
		array[(offset + 3)] = (byte) (int) (val >>> 24 & 0xFF);
	}

	private void toShortBinary(int val, byte[] array, int offset) {
		array[offset] = (byte) (val & 0xFF);
		array[(offset + 1)] = (byte) (val >>> 8 & 0xFF);
	}
}
