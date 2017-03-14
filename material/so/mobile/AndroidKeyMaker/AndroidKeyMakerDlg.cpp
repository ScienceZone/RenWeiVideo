// AndroidKeyMakerDlg.cpp : ʵ���ļ�
//

#include "stdafx.h"
#include "AndroidKeyMaker.h"
#include "AndroidKeyMakerDlg.h"
#include ".\androidkeymakerdlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#endif


// ����Ӧ�ó��򡰹��ڡ��˵���� CAboutDlg �Ի���

class CAboutDlg : public CDialog
{
public:
	CAboutDlg();

// �Ի�������
	enum { IDD = IDD_ABOUTBOX };

	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV ֧��

// ʵ��
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


// CAndroidKeyMakerDlg �Ի���



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


// CAndroidKeyMakerDlg ��Ϣ�������

BOOL CAndroidKeyMakerDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	// ��\������...\���˵�����ӵ�ϵͳ�˵��С�

	// IDM_ABOUTBOX ������ϵͳ���Χ�ڡ�
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

	// ���ô˶Ի����ͼ�ꡣ��Ӧ�ó��������ڲ��ǶԻ���ʱ����ܽ��Զ�
	//  ִ�д˲���
	SetIcon(m_hIcon, TRUE);			// ���ô�ͼ��
	SetIcon(m_hIcon, FALSE);		// ����Сͼ��

	// TODO: �ڴ���Ӷ���ĳ�ʼ������
	
	return TRUE;  // ���������˿ؼ��Ľ��㣬���򷵻� TRUE
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

// �����Ի��������С����ť������Ҫ����Ĵ���
//  �����Ƹ�ͼ�ꡣ����ʹ���ĵ�/��ͼģ�͵� MFC Ӧ�ó���
//  �⽫�ɿ���Զ���ɡ�

void CAndroidKeyMakerDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // ���ڻ��Ƶ��豸������

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// ʹͼ���ڹ��������о���
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// ����ͼ��
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

//���û��϶���С������ʱϵͳ���ô˺���ȡ�ù����ʾ��
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

    // ��ʹ��packageName
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
