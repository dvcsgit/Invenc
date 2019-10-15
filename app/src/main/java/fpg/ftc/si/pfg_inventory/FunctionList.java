package fpg.ftc.si.pfg_inventory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FunctionList extends Activity {

    private Button btnInput;
    private Button btnOutput;
    private Button btnMakeInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_list);

        btnInput=(Button)findViewById(R.id.btn_Input);
        btnOutput=(Button)findViewById(R.id.btn_Output);
        btnMakeInventory=(Button)findViewById(R.id.btn_MakeInventory);

        btnInput.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        Input.class);
                intent.putExtra("Function", "I");
                //finish();
                startActivity(intent);
            }
        });

        btnOutput.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        Input.class);
                intent.putExtra("Function", "O");
                //finish();
                startActivity(intent);
            }
        });

        btnMakeInventory.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        Input.class);
                intent.putExtra("Function", "M");
                //finish();
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.function_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
