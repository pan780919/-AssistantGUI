package com.jhengweipan.tattoofans;

import android.bluetooth.BluetoothDevice;

/**
 * 	This class will parse record from Bluetooth LE "startLEScan()";
 * 	It will parse record when instance this class.
 * 	If just needs to convert record to Hex String, please use "convertRecordToHexString"
 */

public class BleDeviceInfo {
	
	static final public String LOG_TAG = "BluetoothAdvertisementData";
	
	//	Measure in byte.
	static final public int MINIMUM_LENGTH = 30;
	
	static final public int LOCATION_NUMBER_OF_BYTES_IN_FIRST_AD = 0;
	
	static final public int LENGTH_NUM_FIRST_AD_STRUCT = 1;
	static final public int LENGTH_AD_TYPE = 1;
	static final public int LENGTH_FLAG_VALUE = 1;
	static final public int LENGTH_NUM_SECOND_AD_STRUCT = 1;
	static final public int LENGTH_MANUFACTURE_AD_TYPE = 1;
	static final public int LENGTH_COMPANY_ID = 2;
	static final public int LENGTH_AD_INDICATOR_1 = 1;
	static final public int LENGTH_AD_INDICATOR_2 = 1;
	static final public int LENGTH_PROMIXITY_UUID = 16;
	static final public int LENGTH_MAJOR = 2;
	static final public int LENGTH_MINOR = 2;
	static final public int LENGTH_TX_POWER = 1;
	
	static final private char[] HEX_TABLE = "0123456789ABCDEF".toCharArray();
	static final public String SYMBOL_MAC_SEPARATOR = ":";
	static final public String SYMBOL_UUID_SEPARATOR = "-";
	
	private int mNumFirstADStruct = 0;
	private byte mFlagADType = 0;
	private byte mFlagValue = 0;
	private int mNumSecondADStruct = 0;
	private String mManufacturerSpecificADType;
	private String mCompanyID;
	private byte mAdIndicator1 = 0;
	private byte mAdIndicator2 = 0;
	private String mProximityUUID;
	private int mMajor;
	private int mMinor;
	private int mTxPower = 0;
	private String mName;
	private String mMac;
	private int mRssi;
	
	
	/**
	 * 	Parse record.
	 */
	public BleDeviceInfo(BluetoothDevice device, int rssi, byte[] ad)
	{
		if (ad.length < 10) {
			return;
		}
		
		int numFirstADStruct = ad[LOCATION_NUMBER_OF_BYTES_IN_FIRST_AD];
		
		int locationFlagADType = LOCATION_NUMBER_OF_BYTES_IN_FIRST_AD + LENGTH_NUM_FIRST_AD_STRUCT;
		int locationFlagValue = locationFlagADType + LENGTH_AD_TYPE;
		
		int locatinoNumByteSecondAD = LOCATION_NUMBER_OF_BYTES_IN_FIRST_AD + LENGTH_NUM_FIRST_AD_STRUCT + numFirstADStruct;
		int numSecondADStruct = ad[locatinoNumByteSecondAD];
		
		
		int locationManufactureADType = locatinoNumByteSecondAD + LENGTH_NUM_SECOND_AD_STRUCT;
		int locationCompanyID = locationManufactureADType + LENGTH_MANUFACTURE_AD_TYPE;
		int locationADIndicator1 = locationCompanyID + LENGTH_COMPANY_ID;
		int locationADIndicator2 = locationADIndicator1 + LENGTH_AD_INDICATOR_1;
		int locationProximityUUID = locationADIndicator2 + LENGTH_AD_INDICATOR_2;
		int locationMajor = locationProximityUUID + LENGTH_PROMIXITY_UUID;
		int locationMinor = locationMajor + LENGTH_MAJOR;
		int locationTXPower = locationMinor + LENGTH_MINOR;
		
		this.mNumFirstADStruct = numFirstADStruct;
		this.mFlagADType = ad[locationFlagADType];
		this.mFlagValue = ad[locationFlagValue];
		this.mNumSecondADStruct = numSecondADStruct;
		this.mManufacturerSpecificADType = byteToHexString(new byte[] {ad[locationManufactureADType]});
		this.mCompanyID = byteToHexString(copyBytes(ad, locationCompanyID, LENGTH_COMPANY_ID));
		this.mAdIndicator1 = ad[locationADIndicator1];
		this.mAdIndicator2 = ad[locationADIndicator2];
		this.mProximityUUID = addSeparateSymbolToUUID(byteToHexString(copyBytes(ad, locationProximityUUID, LENGTH_PROMIXITY_UUID)));
		this.mMajor = Integer.valueOf(byteToHexString(copyBytes(ad, locationMajor, LENGTH_MAJOR)), 16);
		this.mMinor = Integer.valueOf(byteToHexString(copyBytes(ad, locationMinor, LENGTH_MINOR)), 16);
		this.mTxPower = ad[locationTXPower];
		
		this.mMac = device.getAddress();
		this.mName = device.getName();
		this.mRssi = rssi;
	}
	
	
	//	======================== Convert advertisement Data ========================
	
	
	/**
	 * 	Convert to String match HyXen data collection form.		</br>
	 * 															</br>
	 * 	Data from startLeScan, '01' is a byte.					</br>
	 * 															</br>
	 * 	RSSI: -89												</br>
	 * 	MAC: E0:C7:9D:63:A1:4F									</br>
	 * 	Receive Record : 02011A1AFF4C000215E2C56DB5DFFB48D2B060D0F5A71096E001020304C50909427974657265616C00000000000000000000000000000000000000000000</br>
	 * 															</br>
	 * 	02		Number of bytes that follow in first AD structure. (02 -> 1 Byte)	</br>
	 * 	01		Flag AD type														</br>
	 * 	1A		Flag value 0x1A = 0001 1010*										</br>
	 * 	1A		Number of bytes that follow in second (and last) AD structure.		</br>
	 * 	FF		Manufacturer specific data AD type.									</br>
	 * 	4C 00	company identifier code												</br>
	 * 	02		Byte 0 of iBeacon advertisement indicator.							</br>
	 * 	15		Byte 1 of iBeacon advertisement indicator.							</br>
	 * 	E2 C5 6D B5 DF FB 48 D2 B0 60 D0 F5 A7 10 96 E0	ProximityUUID (16 Bytes)	</br>
	 * 	01 02	Major																</br>
	 * 	03 04	Minor																</br>
	 * 	C5		TX Power 2's complement.											</br>
	 * 																				</br>
	 * 	==== Following is after removing unknown bytes and add-on. ====				</br>
	 * 	41					RSSI is positive value.									</br>
	 * 	E0 C7 9D 63 A1 4F	MAC without colon.										</br>
	 * 
	 */
	static public String[] convertToHyXenUploadData(byte[] record, String mac, int rssi)
	{
//		byte[] data = removeZeroTail(record);

		//	Find meaningful advertisement byte.
//		int numFirstADStruct = record[LOCATION_NUMBER_OF_BYTES_IN_FIRST_AD];
//		int locatinoNumByteSecondAD = LOCATION_NUMBER_OF_BYTES_IN_FIRST_AD + LENGTH_NUM_FIRST_AD_STRUCT + numFirstADStruct;
//		int numSecondADStruct = record[locatinoNumByteSecondAD];
//
//		int adLength = 1 + numFirstADStruct + 1 + numSecondADStruct;	//	Meaningful advertisement data length. First 1 is number of first AD structure, second is second.
		byte[] adByte = copyBytes(record, 0, 30);
		
		String adHex = byteToHexString(adByte);
		
		return new String[] {(adHex + Integer.toHexString(Math.abs(rssi)) + mac.replace(SYMBOL_MAC_SEPARATOR, "")).toUpperCase(), adHex};
	}

	/**
	 * 	Convert record from byte[] to String, and will remove zero at from tail.
	 * 	If you want to parse this record, please instance this class.
	 */
	static public String convertRecordToHexString(byte[] record)
	{
		byte[] nonTail = removeZeroTail(record);
		
		if (nonTail.length < MINIMUM_LENGTH) {
//			Log.e(LOG_TAG, "Bluetooth LE record is too short.");
		}
		
		return byteToHexString(nonTail);
	}
	
	
	//	======================== Widget ========================
	
	
	/**
	 * 	Copy byte data.
	 */
	static public byte[] copyBytes(byte[] data, int from, int length)
	{
		byte[] copyOne = new byte[length];
		for (int i=from; i<from+length; i++) {
			copyOne[i-from] = data[i];
		}
		
		return copyOne;
	}
	
	/**
	 * 	Remove unnecessary "0" from tail to head.
	 */
	static public byte[] removeZeroTail(byte[] data)
	{
		int nonZeroPosition = data.length - 1;
		while (nonZeroPosition > 0 && data[nonZeroPosition] == 0)
		{
			nonZeroPosition--;
		}
		
		return copyBytes(data, 0, nonZeroPosition + 1);
	}
	
	/**
	 * 	Convert byte[] to Hex String.
	 * 	Use query HEX_TABLE to get char as same value;
	 */
	static public String byteToHexString(byte[] data)
	{
		int length = data.length;
		char[] hex = new char[length * 2];	//	1 byte = 2 chars.
		
		for (int i = 0; i < length; i++)
		{
			int value = data[i] & 0xFF;
			hex[i*2] = HEX_TABLE[value >> 4];		//	First byte.
			hex[i*2 + 1] = HEX_TABLE[value & 0x0F];	//	Second byte.
		}
		
		return new String(hex);
	}
	
	/**
	 * 	Add "-" to UUID String at special form like "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0".
	 */
	static public String addSeparateSymbolToUUID(String uuid)
	{
		int[] minusPosition = new int[] {0, 8, 12, 16, 20, 32};
		int section = minusPosition.length - 1;
		String[] buf = new String[section];
		for (int i = 0; i < section; i++) {
			buf[i] = uuid.substring(minusPosition[i], minusPosition[i+1]); 
		}
		
		StringBuffer sb = new StringBuffer();
		int length = buf.length;
		for (int i = 0; i < length; i++)
		{
			sb.append(buf[i]);
			if (i < length - 1) {
				sb.append(SYMBOL_UUID_SEPARATOR);
			}
		}

		return sb.toString();
	}
	
	
//	======================== Getter&Setter ========================
	
	
	public int getNumFirstADStruct() {
		return mNumFirstADStruct;
	}

	public byte getFlagADType() {
		return mFlagADType;
	}

	public byte getFlagValue() {
		return mFlagValue;
	}

	public int getNumSecondADStruct() {
		return mNumSecondADStruct;
	}

	public String getManufacturerSpecificADType() {
		return mManufacturerSpecificADType;
	}

	public String getCompanyID() {
		return mCompanyID;
	}

	public byte getAdIndicator1() {
		return mAdIndicator1;
	}

	public byte getAdIndicator2() {
		return mAdIndicator2;
	}

	public String getProximityUUID() {
		return mProximityUUID;
	}

	public int getMajor() {
		return mMajor;
	}

	public int getMinor() {
		return mMinor;
	}

	public int getTxPower() {
		return mTxPower;
	}

	public String getMac() {
		return mMac == null ? "" : mMac;
	}

	public int getRssi() {
		return mRssi;
	}
	
	public String getName() {
		return mName == null ? "" : mName;
	}
	
}
