package com.database;

    public interface ImageInterface {
        public void onDelete(String message);


       /* holder.imageview_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(arrayList.get(position).getStatus().equalsIgnoreCase("35") ||
                        arrayList.get(position).getStatus().equalsIgnoreCase("40")){
                    ImageView imageView= (ImageView) v;
                    imageView.setImageResource(R.drawable.rcv6);

                    holder.imageview_delivered.getTag(position);
                    // int position = (Integer) v.getTag();

                    if(parent instanceof MyOrderHistory){
                        ((MyOrderHistory)parent).toggle_icon_received_status(position);
                    }

                    Toast.makeText(parent, "Image icon pressed of " + position + " position" , Toast.LENGTH_LONG).show();
                }else{

                }

            }
        });


        public void toggle_icon_received_status(final int ind) {

            // Toast.makeText(parent, "Image icon pressed", Toast.LENGTH_LONG).show();
            //holder.imageview_delivered.setImageResource(R.drawable.rcv6);
            // TODO Auto-generated method stub
            final Dialog myDialog = new Dialog(parent);
            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myDialog.setContentView(R.layout.dialog_message);
            myDialog.setCancelable(true);
            // myDialog.getWindow().setGravity(Gravity.BOTTOM);
            //  myDialog.setTitle("Complete Activity");

            final TextView quest = (TextView) myDialog.findViewById(R.id.textMsg);
            quest.setText("Is shipped products received ?");

            Button btnyes = (Button) myDialog
                    .findViewById(R.id.btn_yes);
            btnyes.setText("YES");
            btnyes.setOnClickListener(new View.OnClickListener() {

                @SuppressLint("NewApi")
                public void onClick(View v) {
                    myDialog.dismiss();
                    // finish();
                }
            });

            Button btnno = (Button) myDialog
                    .findViewById(R.id.btn_no);
            btnno.setText("NO");
            btnno.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    myDialog.dismiss();
                    // finish();
                }
            });

            myDialog.show();

        }*/





    }


