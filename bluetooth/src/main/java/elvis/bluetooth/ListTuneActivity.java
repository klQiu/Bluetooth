package elvis.bluetooth;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListTuneActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ListTuneAdapter mAdapter;
    List<TuneRecord> recordList = new ArrayList<>();
    String title;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tune);
        User user = new User("qq@qq.com","123",6);
        Current.addCurUser(user);
        if ((StartPage.instrumrnt).equals("---") || (StartPage.instrumrnt).equals(null)) {
            title = "My Tunings";
        } else {
            String preTitle = StartPage.instrumrnt + " Tunings";
            title = preTitle.substring(0, 1).toUpperCase() + preTitle.substring(1);
        }
        textView = (TextView) findViewById(R.id.textView);
        textView.setText(title);
        recyclerView = findViewById(R.id.list_tune_recycler);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String instrument = StartPage.instrumrnt;
                //delete it!!!!!
                instrument = "guitar";
                recordList = BackEnd.getHisRecord(Current.getCurUserEmail(), instrument);
            }
        }).start();
        while (recordList.isEmpty()) {}
//        recordList.add(new TuneRecord("notes: ab c d e", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab asdfasd d e", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab c eee", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab ccd e", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab c sdfd e", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab asdfasd d e", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab c eee", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab ccd e", "name: adsfasdf"));
//        recordList.add(new TuneRecord("notes: ab c sdfd e", "name: adsfasdf"));

        // specify an adapter (see also next example)
        mAdapter = new ListTuneAdapter(recordList);
        recyclerView.setAdapter(mAdapter);

    }

    public class ListTuneAdapter extends RecyclerView.Adapter<ListTuneAdapter.ListTuneViewHolder> {
        private List<TuneRecord> recordList;

        public ListTuneAdapter(List<TuneRecord> recordList) {
            this.recordList = recordList;
        }

        @Override
        public ListTuneViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.tune_list_item, parent, false);
            ListTuneViewHolder vh = new ListTuneViewHolder(view);
            return vh;
        }


        @Override
        public void onBindViewHolder(ListTuneViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.notes.setText(recordList.get(position).notes);
            holder.name.setText(recordList.get(position).name);

        }

        @Override
        public int getItemCount() {
            return recordList.size();
        }

        class ListTuneViewHolder extends RecyclerView.ViewHolder {
            TextView notes, name;
            Button more;

            public ListTuneViewHolder(View itemView) {
                super(itemView);
                notes = itemView.findViewById(R.id.tune_list_notes);
                name = itemView.findViewById(R.id.tune_list_name);
                more = itemView.findViewById(R.id.tune_list_three_dots);
                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConfigFragment.getInstance(getAdapterPosition()).show(getFragmentManager(), "itemTuneConfig");
                    }
                });
            }
        }
    }

    public static class ConfigFragment extends DialogFragment {

        public static ConfigFragment getInstance(int pos) {
            ConfigFragment frag = new ConfigFragment();
            Bundle args = new Bundle();
            args.putInt("pos", pos);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_tune_item_config, container, false);
            Button edit = v.findViewById(R.id.tune_item_edit);
            Button delete = v.findViewById(R.id.tune_item_delete);

            final int pos = getArguments().getInt("pos");
            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Code here executes on main thread after user presses button

                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final int id = ((ListTuneActivity) getActivity()).recordList.get(pos).id;
                    // update ui
                    ((ListTuneActivity) getActivity()).recordList.remove(pos);
                    ((ListTuneActivity) getActivity()).mAdapter.notifyItemRemoved(pos);
                    // Code here executes on main thread after user presses button
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BackEnd.deleteNote(id);
                        }
                    }).start();
                    dismiss();
                }
            });
            return v;

        }
    }

    public void add(View view) {
        if ((StartPage.instrumrnt).equals("ukulele")) {
            Intent intent = new Intent(this, chooseNoteActivity.class);
            startActivity(intent);
        } else if ((StartPage.instrumrnt).equals("guitar")) {
            Intent intent = new Intent(this, chooseGuitarActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please choose a valid instrument!", Toast.LENGTH_SHORT).show();
        }
        //should be deleted
//        Intent intent = new Intent(this, chooseGuitarActivity.class);
//        startActivity(intent);
    }

    public void ListReturn(View view) {
        Intent intent = new Intent(this, StartPage.class);
        startActivity(intent);
    }
}
