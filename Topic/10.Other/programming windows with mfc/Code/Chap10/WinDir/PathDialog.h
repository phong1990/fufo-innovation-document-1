#if !defined(AFX_PATHDIALOG_H__18BD7B87_95C6_11D2_8E53_006008A82731__INCLUDED_)
#define AFX_PATHDIALOG_H__18BD7B87_95C6_11D2_8E53_006008A82731__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
// PathDialog.h : header file
//

/////////////////////////////////////////////////////////////////////////////
// CPathDialog dialog

class CPathDialog : public CDialog
{
// Construction
public:
	CPathDialog(CWnd* pParent = NULL);   // standard constructor

// Dialog Data
	//{{AFX_DATA(CPathDialog)
	enum { IDD = IDD_NEWDIR };
	CString	m_strPath;
	//}}AFX_DATA


// Overrides
	// ClassWizard generated virtual function overrides
	//{{AFX_VIRTUAL(CPathDialog)
	protected:
	virtual void DoDataExchange(CDataExchange* pDX);    // DDX/DDV support
	//}}AFX_VIRTUAL

// Implementation
protected:

	// Generated message map functions
	//{{AFX_MSG(CPathDialog)
		// NOTE: the ClassWizard will add member functions here
	//}}AFX_MSG
	DECLARE_MESSAGE_MAP()
};

//{{AFX_INSERT_LOCATION}}
// Microsoft Visual C++ will insert additional declarations immediately before the previous line.

#endif // !defined(AFX_PATHDIALOG_H__18BD7B87_95C6_11D2_8E53_006008A82731__INCLUDED_)
