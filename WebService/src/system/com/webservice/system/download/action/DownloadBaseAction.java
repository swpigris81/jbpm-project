package com.webservice.system.download.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.webservice.system.util.Base64Coder;

/** 
 * <p>Description: [文件下载基类]</p>
 * @author  <a href="mailto: swpigris81@126.com">大牙</a>
 * @version v1.0
 */
public class DownloadBaseAction extends ActionSupport {

    private String fileName;
    private String fileLoc;
    /**
     * <p>Discription:[公共下载文件]</p>
     * @return
     * @throws Exception
     * @author:大牙
     * @update:2012-11-9
     */
    public InputStream getTargetFile() throws Exception{
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/x-download");    //设置为下载application/x-download
        if(this.fileLoc == null || "".equals(fileLoc.trim())){
            return null;
        }
        InputStream target = new FileInputStream(new File(Base64Coder.decodeString(fileLoc)));
        return target;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String fileName.
     */
    public String getFileName() {
        return fileName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param fileName The fileName to set.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @return String fileLoc.
     */
    public String getFileLoc() {
        return fileLoc;
    }
    /**
     * <p>Discription:[方法功能中文描述]</p>
     * @param fileLoc The fileLoc to set.
     */
    public void setFileLoc(String fileLoc) {
        this.fileLoc = fileLoc;
    }
    
    
}
