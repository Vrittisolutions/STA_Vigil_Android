package com.sendimage;

import android.app.Activity;

public class ImageSelectionClaim extends Activity
{
	/*
	String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/Claim Folder";

	//String path = Environment.getExternalStorageDirectory().toString();
	Button btnsend,btncancel;
	ListView lv;
	
	ArrayList<String>fnames=new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.claimimagelistselection);
		
		
		lv=(ListView)findViewById(R.id.listviewimagelist);
		btnsend=(Button)findViewById(R.id.btnsendclaimimages);
		btncancel=(Button)findViewById(R.id.btncancelsendimages);
		
		
		
		getallfiles();
		
		
		
		
		
	}
	
	private void getallfiles() {
		// TODO Auto-generated method stub
		
		
		File f = new File(path);        
		File file[] = f.listFiles();
		
		for (int i=0; i < file.length; i++)
		{
		   
			System.out.println("======== @@$$$ " + file[i].getName());
			
			
			fnames.add(file[i].getName());
			
			
			
			
		}
		
		
		
	}

	public class ImageSelectionAdapter extends BaseAdapter
	{

		Activity context;
		public ImageSelectionAdapter(Activity context ) {
				  super();
				  this.context = context;
				 
				 }
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fnames.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return fnames.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final ViewHolder holder;
			LayoutInflater inflater = context.getLayoutInflater();
			 
			  if (convertView == null) {
			   convertView = inflater.inflate(R.layout.imageselectionadapter, null);
			   holder = new ViewHolder();
			 
			   holder.iv = (ImageView) convertView
			     .findViewById(R.id.ivimagethumb);
			   holder.ck1 = (CheckBox) convertView
			     .findViewById(R.id.chkboximageselection);
			 
			   convertView.setTag(holder);
			 
			  } else {
			    
			   holder = (ViewHolder) convertView.getTag();
			  }
			 
			  
			  holder.ck1.setChecked(false);
			 
			 
			  return convertView;
			 
			 }

		private class ViewHolder {
			  ImageView iv;
			  CheckBox ck1;
			 }
		
	}*/
	
	

}
