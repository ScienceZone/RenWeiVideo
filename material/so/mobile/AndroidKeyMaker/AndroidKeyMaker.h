// AndroidKeyMaker.h : PROJECT_NAME Ӧ�ó������ͷ�ļ�
//

#pragma once

#ifndef __AFXWIN_H__
	#error �ڰ������� PCH �Ĵ��ļ�֮ǰ������stdafx.h��
#endif

#include "resource.h"		// ������


// CAndroidKeyMakerApp:
// �йش����ʵ�֣������ AndroidKeyMaker.cpp
//

class CAndroidKeyMakerApp : public CWinApp
{
public:
	CAndroidKeyMakerApp();

// ��д
	public:
	virtual BOOL InitInstance();

// ʵ��

	DECLARE_MESSAGE_MAP()
};

extern CAndroidKeyMakerApp theApp;
