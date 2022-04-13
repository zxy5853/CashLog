package com.zzr.cash_log.email;

import java.util.Properties;

/**
 * Author: zzr
 * Date: 2021/11/30
 * Desc: 邮件信息类
 */
public class MailSendInfo {

    /**
     * 发送邮件的服务器的IP和端口
     */
    private String mailServerHost;  //例：smtp.lehui.com
    private String mailServerPort;  //例：25
    /**
     * 邮件发送者的地址
     */
    private String fromAddress;     //例：@lehui.com
    /**
     * 邮件接受者的地址
     */
    private String toAddress;       //例：zhangsan@lehui.com
    /**
     * 邮件接受者的地址
     */
    private String[] toAddresses;       //例：[zhangsan@lehui.com]
    /**
     * 登陆邮件发送服务器的用户名和密码
     */
    private String userName;        //例：zhangsan@lehui.com
    private String passWord;        //123445334
    /**
     * 是否需要身份验证
     */
    private boolean validate = false;
    /**
     * 邮件发送的主题
     */
    private String subject;
    /**
     * 邮件发送的内容
     */
    private String content;
    /**
     * 邮件附件的文件名
     */
    private String[] attachFileNames;

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public String[] getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(String[] toAddresses) {
        this.toAddresses = toAddresses;
    }

    /**
     * 获取邮件会话属性
     *
     * @return
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.transport.protocol", "smtp");
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPost() {
        return mailServerPort;
    }

    public void setMailServerPost(String mailServerPost) {
        this.mailServerPort = mailServerPost;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getAttachFileNames() {
        return attachFileNames;
    }

    public void setAttachFileNames(String[] attachFileNames) {
        this.attachFileNames = attachFileNames;
    }


}
