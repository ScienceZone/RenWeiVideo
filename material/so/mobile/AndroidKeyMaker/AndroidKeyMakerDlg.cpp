// AndroidKeyMakerDlg.cpp : 实现文件
//

#include "stdafx.h"
#include "AndroidKeyMaker.h"
#include "AndroidKeyMakerDlg.h"
#include ".\androidkeymakerdlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// 用于应用程序“关于”菜单项的 CAboutDlg 对话框

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// 对话框数据
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV 支持

// 实现
protected:
	DECLARE_MESSAGE_MAP()
};

CAboutDlg::CAboutDlg() : CDialog(CAboutDlg::IDD)
{
}

void CAboutDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CAboutDlg, CDialog)
END_MESSAGE_MAP()


// CAndroidKeyMakerDlg 对话框



CAndroidKeyMakerDlg::CAndroidKeyMakerDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CAndroidKeyMakerDlg::IDD, pParent)
    , m_packageName(_T("com.example.dolitsiteparserdemo"))
    , m_md5(_T("AC:44:70:71:EB:14:1A:D8:9A:84:D2:5A:C6:75:BD:5D"))
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CAndroidKeyMakerDlg::DoDataExchange(CDataExchange* pDX)
{
    CDialog::DoDataExchange(pDX);
    DDX_Control(pDX, IDC_EDIT_MD5, m_keyStoreMD5);
    DDX_Text(pDX, IDC_EDIT_PackageName, m_packageName);
    DDX_Text(pDX, IDC_EDIT_MD5, m_md5);
}

BEGIN_MESSAGE_MAP(CAndroidKeyMakerDlg, CDialog)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	//}}AFX_MSG_MAP
    ON_BN_CLICKED(IDOK, OnBnClickedOk)
END_MESSAGE_MAP()


// CAndroidKeyMakerDlg 消息处理程序

BOOL CAndroidKeyMakerDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// 将\“关于...\”菜单项添加到系统菜单中。

	// IDM_ABOUTBOX 必须在系统命令范围内。
	ASSERT((IDM_ABOUTBOX & 0xFFF0) == IDM_ABOUTBOX);
	ASSERT(IDM_ABOUTBOX < 0xF000);

	CMenu* pSysMenu = GetSystemMenu(FALSE);
	if (pSysMenu != NULL)
	{
		CString strAboutMenu;
		strAboutMenu.LoadString(IDS_ABOUTBOX);
		if (!strAboutMenu.IsEmpty())
		{
			pSysMenu->AppendMenu(MF_SEPARATOR);
			pSysMenu->AppendMenu(MF_STRING, IDM_ABOUTBOX, strAboutMenu);
		}
	}

	// 设置此对话框的图标。当应用程序主窗口不是对话框时，框架将自动
	//  执行此操作
	SetIcon(m_hIcon, TRUE);			// 设置大图标
	SetIcon(m_hIcon, FALSE);		// 设置小图标

	// TODO: 在此添加额外的初始化代码
	
	return TRUE;  // 除非设置了控件的焦点，否则返回 TRUE
}

void CAndroidKeyMakerDlg::OnSysCommand(UINT nID, LPARAM lParam)
{
	if ((nID & 0xFFF0) == IDM_ABOUTBOX)
	{
		CAboutDlg dlgAbout;
		dlgAbout.DoModal();
	}
	else
	{
		CDialog::OnSysCommand(nID, lParam);
	}
}

// 如果向对话框添加最小化按钮，则需要下面的代码
//  来绘制该图标。对于使用文档/视图模型的 MFC 应用程序，
//  这将由框架自动完成。

void CAndroidKeyMakerDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // 用于绘制的设备上下文

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// 使图标在工作矩形中居中
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// 绘制图标
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

//当用户拖动最小化窗口时系统调用此函数取得光标显示。
HCURSOR CAndroidKeyMakerDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

void CAndroidKeyMakerDlg::CopyToClipboard (CString & str)
{
    LPTSTR   pGlobal; 
    HGLOBAL   hGlobal; 

    if(OpenClipboard()) 
    { 
        EmptyClipboard(); 

        hGlobal=GlobalAlloc (GMEM_DDESHARE,  (str.GetLength() + 1) * sizeof(TCHAR)); 
        pGlobal   =   (TCHAR*)GlobalLock(hGlobal); 
        lstrcpy(pGlobal,(LPTSTR)(LPCTSTR)str); 
        GlobalUnlock(hGlobal); 
        SetClipboardData(CF_TEXT,hGlobal); 
        CloseClipboard(); 
    }
}

static unsigned char g_RC4Char[] = {0xe7, 0xbd, 0xb4, 0xfe, 0x41, 0x2d, 0xca, 0xbd, 0x2b, 0x87, 0xd6, 0x57, 0x89, 0x26, 0xce, 0xf1, 0x3, 0x92, 0xcd, 0xed, 0x9e, 0xae, 0xd, 0xbb, 0xf4, 0xfa, 0xc8, 0xdd, 0xc6, 0x9, 0x61, 0x12};

void CAndroidKeyMakerDlg::OnBnClickedOk()
{
    CString strMD5, packageName, strKey1, strKey2;
    GetDlgItemText(IDC_EDIT_MD5, strMD5);
    GetDlgItemText(IDC_EDIT_PackageName, packageName);
    GetDlgItemText(IDC_EDIT_Key1, strKey1);
    GetDlgItemText(IDC_EDIT_Key2, strKey2);

    strMD5.Trim();
    strMD5.Replace(":", "");
    strMD5.MakeLower();
    packageName.MakeLower();
    std::string md5 = (LPCSTR)strMD5;
    std::string strPackageName = (LPCSTR)packageName; 
    
    std::string key1 = (LPCSTR)strKey1;
    std::string key2 = (LPCSTR)strKey2;


    std::string seed = "P1x3a49axW1XuD1u7H";
    seed = md5 + seed + strPackageName + "1&1&9x276543$}a" + key1 + "." + key2;

    // 不使用packageName
    // seed = md5 + seed + ")(*&^flash@#$%a" + key1 + "." + key2;

    std::string g_cert = MD5::Hash(seed);

    if (g_cert.size() == 32)
    {
        CRC4 rc4;
        char buf[32] = {0};
        memcpy(buf, g_cert.c_str(), 32);
        rc4.Encrypt(buf, 32, "xuvt327haww3xAcao083u/MP4.60@1", 30);

        CString str = "static unsigned char g_RC4Char[] = {";
        for (int i = 0; i < 32; i ++)
        {
            CString s;
            if (i < 31)
                s.Format("0x%0x, ", (byte)buf[i]);
            else
                s.Format("0x%0x", (byte)buf[i]);
            str += s;
        }
        str += "};";

        int ret = memcmp(g_RC4Char, buf, 32);


        CopyToClipboard(str);

        str += "\r\n\r\n";

        CString s;
        s.Format("ret: %d", ret);
        str += s;
        SetDlgItemText(IDC_EDIT_INFO, str);
    }
}
