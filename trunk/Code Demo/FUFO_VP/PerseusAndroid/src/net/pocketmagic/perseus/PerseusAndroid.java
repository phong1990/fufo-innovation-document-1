package net.pocketmagic.perseus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class PerseusAndroid extends Activity implements OnClickListener, OnItemClickListener, OnCheckedChangeListener {
	//-- ROBOT --//
	final static String			ROBO_BTADDR				= "00:12:02:15:60:12";
	//-- debugging --//	
	String 						LOG_TAG 				= "FUFO";
	//-- GUI --//
	final static String			m_szAppTitle			= "FUFOAndroid";
	TabHost						m_tabHost;
	ListView					m_lvSearch;	
	ProgressDialog				m_progDlg;
	
	TextView					m_tvD1, m_tvD2, m_tvD3;
	Button                      fwd,bck,rgt,lft,up,down;     
	//-- Bluetooth functionality --//
	
	//BTNative					m_BT;					//obsolete
	final static int			MAX_DEVICES				= 50;
	 
	BluetoothAdapter 			m_BluetoothAdapter;
	boolean						m_ASDKScanRunning		= false;
	int							m_nDiscoverResult 		= -1;
	int							m_nRoboDev				= -1;
	final Handler 				m_Handler 				= new Handler();	//used for discovery thread, etc
    // Intent request codes
    final 		int 			REQUEST_CONNECT_DEVICE 	= 1,
    							REQUEST_ENABLE_BT 		= 2;
    BluetoothSocket 			m_btSck;									//used to handle Android<->Robot communication
    private static final UUID 	SPP_UUID 				= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Thread						m_hReadThread;

	
	
	public static final int 	idMenuTab1Search		= Menu.FIRST +   1,
								idTab2FWD			= Menu.FIRST + 2,
								idTab2BCK			= Menu.FIRST + 3,
								idTab2LFT			= Menu.FIRST + 4,
								idTab2RGT			= Menu.FIRST + 5,
								idTab2UP            = Menu.FIRST + 6,
								idTab2DWN           = Menu.FIRST + 7,
								idLVFirstItem			= Menu.FIRST + 100;	

	class BTDev {
		String 	m_szName;
		String 	m_szAddress;
		int		m_nBTDEVType; //if 1, it's the Perseus ROBOT, if 0 it's a regular device
		
		
		BTDev(String _name, String _address) {
			m_szName = _name; m_szAddress = _address;  
		}
		BTDev(String _name, String _address, int _type) {
			m_szName = _name; m_szAddress = _address; m_nBTDEVType = _type;  
		}
	}
	BTDev	BTDevs[];
	int		BTCount;
	
	
	
	private View createTabContent1()
	{
		final Context context = PerseusAndroid.this;
		
		// Tab container
    	LinearLayout panel = new LinearLayout(context);
  		panel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
  		panel.setOrientation(LinearLayout.VERTICAL);
  		
  		LinearLayout panelH = new LinearLayout(context);
     	panelH.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
  		panelH.setOrientation(LinearLayout.HORIZONTAL);
  		panelH.setGravity(Gravity.LEFT);
  		panelH.setGravity(Gravity.CENTER_VERTICAL);

  		Button but = new Button(this);
  		but.setText("Search BT Devices");
  		but.setId(idMenuTab1Search);
  		but.setOnClickListener(this);
  		but.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
     	panelH.addView(but);
     	
     	panel.addView(panelH);


		m_lvSearch = new ListView( this );
	 	// clear previous results in the LV
		m_lvSearch.setAdapter(null);      
		m_lvSearch.setOnItemClickListener((OnItemClickListener) this);
		
		/*// -- remove this
		BTDevs[BTCount] = new BTDev("test", ROBO_BTADDR, 1);
		BTCount++;
		
		PopulateLV();
		// -- end*/
	    panel.addView(m_lvSearch);
	    TextView lbBottom = new TextView(context);
    	lbBottom.setText("Press the button to discover Bluetooth devices");
    	lbBottom.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
     	panel.addView(lbBottom);
     	
     	
	    
	    return panel;
	}
	
	LinearLayout panelH1;
	LinearLayout panelH2;
	LinearLayout panelH3;
	Button but;
	public RadioGroup checkControl;
	private View createTabContent2()
	{
		final Context context = PerseusAndroid.this;
		
		// Tab container
    	LinearLayout panel = new LinearLayout(context);
    	panel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
    	panel.setOrientation(LinearLayout.VERTICAL);
    	 checkControl = new RadioGroup(context);
    	RadioButton checkControl1 = new RadioButton(context);
    	checkControl1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
    	checkControl1.setText("Control via computer!");
    //	panel.addView(checkControl1);
    	RadioButton checkControl2 = new RadioButton(context);
    	checkControl2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
    	checkControl2.setText("Control via Android Phone!");
    	checkControl1.setId(5555);
    	checkControl2.setId(5533);
    	checkControl.addView(checkControl1);
    	checkControl.addView(checkControl2);
    	checkControl.check(5533);
    	checkControl.setOnCheckedChangeListener(this);
  
        panel.addView(checkControl);
    	
    	LinearLayout panelH1 = new LinearLayout(context);
  		panelH1.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
  		panelH1.setOrientation(LinearLayout.HORIZONTAL);
  		panelH1.setGravity(Gravity.CENTER);
  		panelH1.setGravity(Gravity.CENTER_VERTICAL);
        but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH1.addView(but);
     	but.setVisibility(4);
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH1.addView(but);
        but.setVisibility(4);
     	fwd = new Button(this);
     	fwd.setText("FWD");
     	fwd.setGravity(Gravity.CENTER);
     	fwd.setId(idTab2FWD);
     	fwd.setOnClickListener(this);
     	fwd.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
     	panelH1.addView(fwd);
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH1.addView(but);
     	but.setVisibility(4);
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH1.addView(but);
     	but.setVisibility(4);
     	up = new Button(this);
     	up.setText("UUP");
     	up.setGravity(Gravity.CENTER);
     	up.setId(idTab2UP);
     	up.setOnClickListener(this);
        panelH1.addView(up);
        up.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH1.addView(but);
     	but.setVisibility(4);
     	
     	panel.addView(panelH1);
     	
     	LinearLayout panelH2 = new LinearLayout(context);
     	panelH2.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
  		panelH2.setOrientation(LinearLayout.HORIZONTAL);
  		panelH2.setGravity(Gravity.CENTER);
  		panelH2.setGravity(Gravity.CENTER_VERTICAL);
  		but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH2.addView(but);
        but.setVisibility(4);
  		lft = new Button(this);
  		lft.setText("LFT");
  		lft.setId(idTab2LFT);
  		lft.setOnClickListener(this);
  		lft.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 		
     	panelH2.addView(lft);
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH2.addView(but);
     	but.setVisibility(4);
     	rgt = new Button(this);
     	rgt.setText("RGT");
     	rgt.setId(idTab2RGT);
     	rgt.setOnClickListener(this);
     	rgt.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
     	panelH2.addView(rgt);
     	panel.addView(panelH2);
     	
     	LinearLayout panelH3 = new LinearLayout(context);
     	panelH3.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
  		panelH3.setOrientation(LinearLayout.HORIZONTAL);
  		panelH3.setGravity(Gravity.CENTER);
  		panelH3.setGravity(Gravity.CENTER_VERTICAL);
  		but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH3.addView(but);
        but.setVisibility(4);
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH3.addView(but);
     	but.setVisibility(4);
     	bck = new Button(this);
     	bck.setText("BCK");
     	bck.setId(idTab2BCK);
     	bck.setOnClickListener(this);
     	bck.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
     	panelH3.addView(bck);
     	but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH3.addView(but);
     	but.setVisibility(4);
        but = new Button(this);but.setText("____");but.setEnabled(false);but.setFocusable(false);but.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));panelH3.addView(but);
        but.setVisibility(4);
        down = new Button(this);
        down.setText("DWN");
        down.setGravity(Gravity.CENTER);
        down.setId(idTab2DWN);
        down.setOnClickListener(this);
        panelH3.addView(down);
        down.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
     	panel.addView(panelH3);
   

  		
  		return panel;
	}
	
	/** This function creates the Main interface: the TAB host **/
	private View createMainTABHost() {
		// construct the TAB Host
    	TabHost tabHost = new TabHost(this);
    	tabHost.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
 
        // the tabhost needs a tabwidget, that is a container for the visible tabs
        TabWidget tabWidget = new TabWidget(this);
        tabWidget.setId(android.R.id.tabs);
        tabHost.addView(tabWidget, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
 
        // the tabhost needs a frame layout for the views associated with each visible tab
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setId(android.R.id.tabcontent);
        frameLayout.setPadding(0, 65, 0, 0);
        tabHost.addView(frameLayout, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); 
 
        // setup must be called if you are not initialising the tabhost from XML
        tabHost.setup(); 
 
        // create the tabs
        TabSpec ts;
        ImageView iv;
        
        ts = tabHost.newTabSpec("TAB_TAG_1");
        ts.setIndicator("Setting");
        ts.setContent(new TabHost.TabContentFactory()
        {
            public View createTabContent(String tag)
            {
            	return createTabContent1();
             } //TAB 1 done
        });
        tabHost.addTab(ts);
        // -- set the image for this tab
        iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.icon);
        if (iv != null) iv.setImageDrawable(getResources().getDrawable(R.drawable.bt));

 
        ts = tabHost.newTabSpec("TAB_TAG_2");
        ts.setIndicator("Control");
        ts.setContent(new TabHost.TabContentFactory(){
             public View createTabContent(String tag)
             {
            	 return createTabContent2();
             }
        });
        tabHost.addTab(ts);
        // -- set the image for this tab
        iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.icon);
        if (iv != null) iv.setImageDrawable(getResources().getDrawable(R.drawable.perseus));
        
        return tabHost;
	}
    
	 /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //m_BT = new BTNative();
        BTDevs = new BTDev[MAX_DEVICES]; 
        Log.d("FUFO", "da connect bt");
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (m_BluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (!m_BluetoothAdapter.isEnabled()) 
        {
        	// enable bluetooth
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }  
        
        
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter); 

        
        // disable the titlebar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // create the interface
        m_tabHost = (TabHost)createMainTABHost();
    	
        setContentView(m_tabHost);
    }
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
                        
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //if (device.getBondState() != BluetoothDevice.BOND_BONDED) // If it's already paired, skip it, because it's been listed already
            	//-- ignore duplicates
				boolean duplicate = false;
				for (int j=0;j<BTCount;j++)
					if (BTDevs[j].m_szAddress.compareTo(device.getAddress()) == 0) { duplicate = true; break; }
				if (duplicate)
					; //this is a duplicate
				else
				{
					if (device.getAddress().compareTo(ROBO_BTADDR) == 0)
						BTDevs[BTCount] = new BTDev(device.getName(), device.getAddress(), 1);
					else
						BTDevs[BTCount] = new BTDev(device.getName(), device.getAddress(), 0);
	                BTCount++;
				}
                
            // When discovery is finished
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	m_ASDKScanRunning = false; 
            }
        }
    };	


	@Override
	public void onClick(View v) {
		int cmdId = v.getId();
		if (cmdId == idMenuTab1Search)
		{
			startDiscoverBluetoothDevices();
		}
		if (cmdId == idTab2FWD)
		{
			if (m_btSck != null)
				try {
					m_btSck.getOutputStream().write('w');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (cmdId == idTab2LFT)
		{
			if (m_btSck != null)
				try {
					m_btSck.getOutputStream().write('a');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (cmdId == idTab2RGT)
		{
			if (m_btSck != null)
				try {
					m_btSck.getOutputStream().write('d');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (cmdId == idTab2BCK)
		{
			if (m_btSck != null)
				try {
					m_btSck.getOutputStream().write('s');
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if (cmdId == idTab2UP)
        {
            if (m_btSck != null)
                try {
                    m_btSck.getOutputStream().write('o');
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
		if (cmdId == idTab2DWN)
        {
            if (m_btSck != null)
                try {
                    m_btSck.getOutputStream().write('p');
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (m_tabHost.getCurrentTab() == 0) //we are on SEARCH page (0)
		{
			int nIndex = -1, nCounter = 0;
			for (int i=0;i<BTCount;i++)
			{
				if (arg2 == nCounter) {
					nIndex = i;
					break;
				}
				nCounter++;
			}
			// connect to 
			if (BTDevs[nIndex].m_nBTDEVType == 1)
			{
				//connect to ROBOT
				Connect(nIndex);
				StartReadThread(nIndex);
			}
			else 
				Toast.makeText(getBaseContext(), 
						"This is not ROBO", Toast.LENGTH_SHORT).show();


		}
		
	}
	
	// put the /BTDEvs in the listview
	void PopulateLV()
	{
		ArrayList<Device> m_Devices = new ArrayList<Device>();
		Device device;
        for (int i=0;i<BTCount;i++) {
        	if (BTDevs[i].m_szAddress.compareTo(PerseusAndroid.ROBO_BTADDR) == 0) {
        		BTDevs[i].m_nBTDEVType = 1;
        		m_nRoboDev = i;
        	}
        	else 
        		BTDevs[i].m_nBTDEVType = 0;
        	device = new Device(BTDevs[i].m_szName, 
        			BTDevs[i].m_szAddress, 
        			BTDevs[i].m_nBTDEVType,
        			0, 
        			idLVFirstItem+i);
        	m_Devices.add(device);
        }
    CustomAdapter lvAdapter =  new CustomAdapter(this, m_Devices);
    if (lvAdapter!=null) m_lvSearch.setAdapter(lvAdapter);
    if (m_nRoboDev >= 0)
    	Toast.makeText(getBaseContext(), "ROBO found as " + BTDevs[m_nRoboDev].m_szAddress, 
    			Toast.LENGTH_LONG).show();
	}
	
	/** Bluetooth Functions **/
	
	// not Blocking, uses events
	int ASDKDiscoverBluetoothDevices()
	{
		if (m_BluetoothAdapter.isDiscovering()) 
    		m_BluetoothAdapter.cancelDiscovery();
        
		int current_devs = BTCount;
		// Request discover from BluetoothAdapter
    	if (!m_BluetoothAdapter.startDiscovery()) return -1; //error
    	
    	m_ASDKScanRunning = true;

    	//  blocking operation:wait to complete
        while (m_ASDKScanRunning);
         
        return BTCount - current_devs;
	}

	final Runnable mUpdateResultsDiscover = new Runnable() {
        public void run() {
        	doneDiscoverBluetoothDevices();
        }
    };
    protected void startDiscoverBluetoothDevices() {
    	// Show Please wait dialog
    	m_progDlg = ProgressDialog.show(this,
    			m_szAppTitle, "Scanning, please wait",
    			true);
    	
    	// Fire off a thread to do some work that we shouldn't do directly in the UI thread
	    Thread t = new Thread() {
	    	public void run() 
	    	{
	    		// blocking operation
            		m_nDiscoverResult = ASDKDiscoverBluetoothDevices();
            	//show results
	        	m_Handler.post(mUpdateResultsDiscover);
	    	}
	    };
	    t.start();
    }
    
    private void doneDiscoverBluetoothDevices() 
    {
    	m_progDlg.dismiss();
    	if (m_nDiscoverResult == -1)
    		Toast.makeText(getBaseContext(), "Bluetooth ERROR (is bluetooth on?)", Toast.LENGTH_LONG).show();
    	else if (m_nDiscoverResult == 0)
    		Toast.makeText(getBaseContext(), "No Bluetooth devices found", Toast.LENGTH_LONG).show();
    	else {
    		m_nRoboDev = -1;
    		// populate
			PopulateLV();
    	}
    }
    int Connect(int nIndex)
	{
		if (nIndex >= BTCount || nIndex<0) return -1; //invalid device
		
		//ANDROID SDK IMPLEMENTATION
		//--connect serial port
		BluetoothDevice ROBOBTDev = m_BluetoothAdapter.getRemoteDevice(BTDevs[nIndex].m_szAddress);
		try {
			m_btSck = ROBOBTDev.createRfcommSocketToServiceRecord(SPP_UUID);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try { //This is a blocking call and will only return on a successful connection or an exception
			m_btSck.connect();	
			Log.d("FUFO", "da connect bt");
		} catch (IOException e) {
             // Close the socket
             try { m_btSck.close();} catch (IOException e2) { e2.printStackTrace();}
             return -2; 
         }
		return 0;
	}
    int Disconnect(int nIndex)
	{
		if (nIndex >= BTCount || nIndex<0) return -1; //invalid device
		
		// DISCONNECT ASDK SOCKETS
		if (m_btSck != null) {
			try {
				m_btSck.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
            }
			m_btSck = null;
		}
		return 0;
	}

    // Worker functions
	int StartReadThread(final int nIndex)
	{
		// signal connect event for this BT dev
		ConnectionEvent(1,nIndex,null);
		
		m_hReadThread = new Thread() {
	        public void run() 
	        {
	        	
	        	while (true) 
	        	{
	        		byte buf[] = null;
        		
	        		try {
	                    // Read from the InputStream
	        			byte[] buffer = new byte[1024]; 
	        			int bread = m_btSck.getInputStream().read(buffer);
	        			buf = new byte[bread];
	        			System.arraycopy(buffer, 0, buf, 0, bread);
	        			

	                    // Send the obtained bytes to the UI Activity
	                    Log.i(LOG_TAG, "StartReadThread: Data received:"+ bread);
	                    Log.i(LOG_TAG, "StartReadThread: Data received:"+ "day");
	                } catch (IOException e) {
	                	Log.d(LOG_TAG, "StartReadThread: disconnected", e);
	                } 
	        		
	        		// signal disconnect event
		        	if (buf == null)
		        	{
		        		ConnectionEvent(2,nIndex, null);
		        		break;
		        	}
	        		else //signal incoming data
	        		{
	        			ConnectionEvent(3,nIndex, buf);
	        		}
	        	}
	        }
        };
        m_hReadThread.start();
		return 0;
	}
	
	// Worker event function called on various events
	int ConnectionEvent(int type, int nDevId, byte buf[])
	{
		if (nDevId >= BTCount)return -1;
		
		if (type == 1) { //connected
			m_tabHost.post(new Runnable() { public void run() {m_tabHost.setCurrentTab(1);} });
		}
		if (type == 2) { //disconnect
			// DISCONNECT NATIVE SOCKETS
			//Toast.makeText(this, "Disconnected from ROBO", Toast.LENGTH_LONG).show();
			Disconnect(nDevId);
			
			m_tabHost.post(new Runnable() { public void run() {m_tabHost.setCurrentTab(0);} });
		}
		if (type == 3) { 
			if (buf.length == 0) return -1;
			
			int nTHeader = buf[0]& 0xFF; 
			int nTType = (nTHeader >>> 4) & 0xF;	//transaction type
			int nTParam = (nTHeader) & 0xF;		//transaction parameters
			
			
		}
		return 0;
	}

    /**
     * [Explain the description for this method here].
     * @param arg0
     * @param arg1
     * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
     */
    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        // TODO Auto-generated method stub
        if(arg0.getCheckedRadioButtonId() == 5555){
         
           fwd.setEnabled(false);
           bck.setEnabled(false);
           lft.setEnabled(false);
           rgt.setEnabled(false);
           up.setEnabled(false);
           down.setEnabled(false);
        }
        else {
            fwd.setEnabled(true);
            bck.setEnabled(true);
            lft.setEnabled(true);
            rgt.setEnabled(true);
            up.setEnabled(true);
            down.setEnabled(true);
        }
        
    }


}

