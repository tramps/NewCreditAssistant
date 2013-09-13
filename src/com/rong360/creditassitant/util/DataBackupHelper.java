package com.rong360.creditassitant.util;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;

public class DataBackupHelper {
    private static final String TAG = DataBackupHelper.class.getSimpleName();

    private static final String DEFAULT_FOLDER = "rongyiji";
    private static final String exportFile = "ryj.csv";

    private static final String COMMA = ",";
    private static final String TAB = "\t";
    private static final String LINE_BREAK = "\r\n";

    public static boolean hasSDCard() {
	if (Environment.getExternalStorageState().equals(
		Environment.MEDIA_MOUNTED))
	    return true;
	else
	    return false;
    }

    public static File getFolder(Context context, String folderName,
	    boolean permitInRom) {
	File file = null;
	if (hasSDCard()) {
	    file =
		    new File(Environment.getExternalStorageDirectory(),
			    folderName);
	} else {
	    try {
		if (permitInRom) {
		    folderName = folderName.replaceAll("/", "_");
		    file = context.getDir(folderName, Context.MODE_PRIVATE);
		}
	    } catch (Exception e) {
	    }
	}
	try {
	    if (file != null)
		file.mkdirs();
	} catch (Exception e) {
	}
	return file;
    }

    public static File getFile(Context context, String folderName,
	    String fileName) {
	File folder = getFolder(context, folderName, false);
	if (folder == null)
	    return null;
	return new File(folder, fileName);
    }

    public static void backupCustomers(Context context) {
	Handler handler = new Handler(context.getMainLooper());
	handler.post(new Export2CSV(context));
    }

    private static class Export2CSV extends Thread {
	private Context mContext;

	public Export2CSV(Context context) {
	    mContext = context;
	}

	@Override
	public void run() {
	    File exportedFile =
		    DataBackupHelper.getFile(mContext, DEFAULT_FOLDER,
			    exportFile);
	    if (exportedFile == null) {
		Toast.makeText(mContext, "create file failed",
			Toast.LENGTH_SHORT).show();
		return;
	    }

	    Resources res = mContext.getResources();
	    String[][] arrays = new String[5][];
	    arrays[0] = res.getStringArray(R.array.bankCash);
	    arrays[1] = res.getStringArray(R.array.identity);
	    arrays[2] = res.getStringArray(R.array.credit);
	    arrays[3] = res.getStringArray(R.array.house);
	    arrays[4] = res.getStringArray(R.array.car);
	    StringBuilder sb;
	    char[] buf = null;
	    try {
		Writer writer = new FileWriter(exportedFile);
		List<Customer> customers =
			GlobalValue.getIns().getAllCustomers();
		Log.i(TAG, "size: " + customers.size());
		sb = new StringBuilder();
		sb.append("姓名").append(TAB);
		sb.append("电话").append(TAB);
		sb.append("贷款").append(TAB);
		sb.append("进度").append(TAB);
		sb.append("银行流水").append(TAB);
		sb.append("现金流水").append(TAB);
		sb.append("身份").append(TAB);
		sb.append("信用记录").append(TAB);
		sb.append("房产").append(TAB);
		sb.append("车辆").append(TAB);
		sb.append(LINE_BREAK);
		buf = sb.toString().toCharArray();
		writer.write(buf, 0, buf.length);
		for (Customer c : customers) {
		    sb = new StringBuilder();
		    sb.append(c.getName()).append(COMMA).append(TAB)
			    .append(TAB);
		    sb.append(c.getTel()).append(COMMA).append(TAB).append(TAB);
		    sb.append(c.getLoan()).append(COMMA).append(TAB)
			    .append(TAB);
		    String progress = c.getProgress();
		    if (c.getProgress() != null) {
			progress = "无";
		    }
		    sb.append(progress).append(COMMA).append(TAB).append(TAB);
		    if (c.getBank() == -1) {
			sb.append("无").append(COMMA).append(TAB).append(TAB);
		    } else {
			sb.append(arrays[0][c.getBank()]).append(COMMA)
				.append(TAB).append(TAB);
		    }

		    if (c.getCash() == -1) {
			sb.append("无").append(COMMA).append(TAB).append(TAB);
		    } else {
			sb.append(arrays[0][c.getCash()]).append(COMMA)
				.append(TAB).append(TAB);
		    }

		    if (c.getCreditRecord() == -1) {
			sb.append("无").append(COMMA).append(TAB).append(TAB);
		    } else {
			sb.append(arrays[0][c.getCreditRecord()]).append(COMMA)
				.append(TAB).append(TAB);
		    }

		    if (c.getHouse() == -1) {
			sb.append("无").append(COMMA).append(TAB).append(TAB);
		    } else {
			sb.append(arrays[0][c.getHouse()]).append(COMMA)
				.append(TAB).append(TAB);
		    }

		    if (c.getCar() == -1) {
			sb.append("无").append(COMMA).append(TAB).append(TAB);
		    } else {
			sb.append(arrays[0][c.getCar()]).append(COMMA)
				.append(TAB).append(TAB);
		    }

		    // sb.append(c.getCash()).append(COMMA).append(TAB)
		    // .append(TAB);
		    // sb.append(c.getIdentity()).append(COMMA).append(TAB)
		    // .append(TAB);
		    // sb.append(c.getCreditRecord()).append(COMMA).append(TAB)
		    // .append(TAB);
		    // sb.append(c.getHouse()).append(COMMA).append(TAB)
		    // .append(TAB);
		    // sb.append(c.getCar()).append(COMMA).append(TAB).append(TAB);
		    sb.append(LINE_BREAK);
		    buf = sb.toString().toCharArray();
		    writer.write(buf, 0, buf.length);
		}

		writer.flush();
		writer.close();

		Toast.makeText(mContext,
			"已备份到" + DEFAULT_FOLDER + "/" + exportFile,
			Toast.LENGTH_LONG).show();
		Log.i(TAG, "Write to file: " + exportedFile.getAbsolutePath()
			+ ", size:" + exportedFile.length() / 1024 + "kb");
	    } catch (Exception e) {
		Log.e(TAG, e.toString());
		Toast.makeText(mContext, "备份失败", Toast.LENGTH_SHORT).show();
	    }
	}
    }
}
