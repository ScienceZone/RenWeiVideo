// AndroidKeyMakerDlg.h : 头文件
//

#pragma once
#include "afxwin.h"


// CAndroidKeyMakerDlg 对话框
class CAndroidKeyMakerDlg : public CDialog
{
// 构造
public:
	CAndroidKeyMakerDlg(CWnd* pParent = NULL);	// 标准构造函数

// 对话框数据
	enum { IDD = IDD_ANDROIDKEYMAKER_DIALOG };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);	// DDX/DDV 支持


// 实现
protected:
	HICON m_hIcon;

	// 生成的消息映射函数
	virtual BOOL OnInitDialog();
	afx_msg void OnSysCommand(UINT nID, LPARAM lParam);
	afx_msg void OnPaint();
	afx_msg HCURSOR OnQueryDragIcon();
	DECLARE_MESSAGE_MAP()
public:
    afx_msg void OnBnClickedOk();
    void CopyToClipboard (CString & str);
    CEdit m_keyStoreMD5;
    CString m_packageName;
    CString m_md5;
};
