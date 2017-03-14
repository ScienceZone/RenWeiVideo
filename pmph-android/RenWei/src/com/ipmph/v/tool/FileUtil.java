package com.ipmph.v.tool;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FileUtil {

	public static double Rounding(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd.setScale(1, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
	 public static String formatFileSize(long fileS) {
	        String fileSizeString = "";
	        if (fileS <= 0) {
	            return "0 KB";
	        }
	        if (fileS < 1024) {
	            fileSizeString = "1 KB";
	        } else if (fileS < 1048576) {
	            long inte = fileS / 1024;
	            if (inte > 100) {
	                DecimalFormat df = new DecimalFormat("##0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1024)) + " KB";
	            } else if (inte > 10) {
	                DecimalFormat df = new DecimalFormat("##0.0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1024)) + " KB";
	            } else {
	                DecimalFormat df = new DecimalFormat("##0.00");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1024)) + " KB";
	            }
	        } else if (fileS < 1073741824) {
	            long inte = fileS / 1048576;
	            if (inte > 100) {
	                DecimalFormat df = new DecimalFormat("##0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1048576)) + " MB";
	            } else if (inte > 10) {
	                DecimalFormat df = new DecimalFormat("##0.0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1048576)) + " MB";
	            } else {
	                DecimalFormat df = new DecimalFormat("##0.00");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1048576)) + " MB";
	            }
	        } else if (fileS < 1099511627776L) {
	            long inte = fileS / 1073741824;
	            if (inte > 100) {
	                DecimalFormat df = new DecimalFormat("##0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1073741824)) + " GB";
	            } else if (inte > 10) {
	                DecimalFormat df = new DecimalFormat("##0.0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1073741824)) + " GB";
	            } else {
	                DecimalFormat df = new DecimalFormat("##0.0");
	                fileSizeString = df.format(FileUtil.Rounding((double) fileS / 1073741824)) + " GB";
	            }
	        } else {
	            long inte = fileS / 1099511627776L;
	            if (inte > 100) {
	                DecimalFormat df = new DecimalFormat("##0");
	                fileSizeString = df.format((double) fileS / 1099511627776L) + " TB";
	            } else if (inte > 10) {
	                DecimalFormat df = new DecimalFormat("##0.0");
	                fileSizeString = df.format((double) fileS / 1099511627776L) + " TB";
	            } else {
	                DecimalFormat df = new DecimalFormat("##0.00");
	                fileSizeString = df.format((double) fileS / 1099511627776L) + " TB";
	            }
	        }
	        return fileSizeString;
	    }
	 public static String getfileName(String url) {

			return url.substring(url.lastIndexOf("/") + 1);
		}
	 public static long getFolderSize(File file) throws Exception {  
	        long size = 0;  
	        try {  
	            File[] fileList = file.listFiles();  
	            for (int i = 0; i < fileList.length; i++) {  
	                // 如果下面还有文件  
	                if (fileList[i].isDirectory()) {  
	                    size = size + getFolderSize(fileList[i]);  
	                } else {  
	                    size = size + fileList[i].length();  
	                }  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
	        return size;  
	    }  
	       
	    /** 
	     * 格式化单位 
	     *  
	     * @param size 
	     * @return 
	     */ 
	    public static String getFormatSize(long size) {  
	        double kiloByte = size / 1024;  
	        if (kiloByte < 1) {  
//	            return size + "Byte";  
	            return "0K";
	        }  
	   
	        double megaByte = kiloByte / 1024;  
	        if (megaByte < 1) {  
	            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));  
	            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)  
	                    .toPlainString() + "KB";  
	        }  
	   
	        double gigaByte = megaByte / 1024;  
	        if (gigaByte < 1) {  
	            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));  
	            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)  
	                    .toPlainString() + "MB";  
	        }  
	   
	        double teraBytes = gigaByte / 1024;  
	        if (teraBytes < 1) {  
	            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));  
	            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)  
	                    .toPlainString() + "GB";  
	        }  
	        BigDecimal result4 = new BigDecimal(teraBytes);  
	        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()  
	                + "TB";  
	    }  
	    
	}
