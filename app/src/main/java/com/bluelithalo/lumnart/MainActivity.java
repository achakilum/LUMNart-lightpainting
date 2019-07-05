package com.bluelithalo.lumnart;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.bluelithalo.lumnart.editor.EditTextDialogFragment;
import com.bluelithalo.lumnart.editor.PatternEditorActivity;
import com.bluelithalo.lumnart.pattern.Pattern;
import com.bluelithalo.lumnart.util.SnackbarFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,
                                                                PatternListAdapter.PatternListContainer,
                                                                DirectoryListAdapter.DirectoryListContainer,
                                                                NewPatternDirectoryDialog.OnConfirmPatternDirectoryListener,
                                                                DirectoryOptionsDialog.OnConfirmDirectoryOptionListener,
                                                                PatternOptionsDialog.OnConfirmPatternOptionListener,
                                                                MovePatternDialog.OnConfirmMovePatternListener,
                                                                EditTextDialogFragment.OnConfirmEditTextListener
{
    private static final String HOME_DIRECTORY_NAME = "LUMNart-HOME";
    
    private static final String NEW_PATTERN_DIRECTORY = "Create something new...";
    private static final String NEW_PATTERN_NAME = "New pattern name";
    private static final String NEW_DIRECTORY_NAME = "New folder name";

    private static final String PATTERN_OPTIONS = "Pattern options";
    private static final String MOVE_PATTERN = "Move pattern";
    private static final String DUPLICATE_PATTERN = "Duplicate pattern";
    private static final String DELETE_PATTERN = "Delete pattern";

    private static final String DIRECTORY_OPTIONS = "Folder options";
    private static final String RENAME_DIRECTORY = "Rename folder";
    private static final String DELETE_DIRECTORY = "Delete folder";

    private RecyclerView directoryRecyclerView;
    private RecyclerView.Adapter directoryRVAdapter;
    private RecyclerView.LayoutManager directoryRVLayoutManager;

    private RecyclerView patternRecyclerView;
    private RecyclerView.Adapter patternRVAdapter;
    private RecyclerView.LayoutManager patternRVLayoutManager;

    private String currentPath;
    private static String dataPath = null;
    private ArrayList<String> directoryFilePathList;
    private ArrayList<String> patternFilePathList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                invokePatternDirectoryDialog(NEW_PATTERN_DIRECTORY);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        generateFirstPatternFiles();
        directoryFilePathList = new ArrayList<String>();
        patternFilePathList = new ArrayList<String>();
        
        if (dataPath == null)
        {
            dataPath = getFilesDir().getAbsolutePath();
        }

        if (savedInstanceState != null)
        {
            currentPath = savedInstanceState.getString("currentPath");
            updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        }
        else
        {
            currentPath = getFilesDir().getAbsolutePath() + "/LUMNart-HOME";
            updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        }

        directoryRecyclerView = (RecyclerView) findViewById(R.id.directory_recycler_view);
        directoryRecyclerView.setHasFixedSize(true);

        directoryRVLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        directoryRecyclerView.setLayoutManager(directoryRVLayoutManager);

        directoryRVAdapter = new DirectoryListAdapter(directoryFilePathList, this);
        directoryRecyclerView.setAdapter(directoryRVAdapter);

        directoryRecyclerView.setNestedScrollingEnabled(true);

        patternRecyclerView = (RecyclerView) findViewById(R.id.pattern_recycler_view);
        patternRecyclerView.setHasFixedSize(true);

        patternRVLayoutManager = new LinearLayoutManager(this);
        patternRecyclerView.setLayoutManager(patternRVLayoutManager);

        patternRVAdapter = new PatternListAdapter(patternFilePathList, this);
        patternRecyclerView.setAdapter(patternRVAdapter);

        patternRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        patternRVAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Log.i("MainActivity", "onSaveInstanceState()");
        outState.putString("currentPath", currentPath);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pattern_browser)
        {
            // Handle the camera action
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchGLTestActivity()
    {
        Intent i = new Intent(this, GLTestActivity.class);
        startActivity(i);
    }

    private void updateListForDirectory(File dir, List<String> filePathList, List<String> directoryPathList)
    {
        filePathList.clear();
        directoryPathList.clear();
        directoryPathList.add(new File(currentPath).getParent());

        try
        {
            currentPath = dir.getCanonicalPath() + "/";
            //Log.i("HomeActivity", "Directory: " + currentPath);
        }
        catch (IOException e)
        {
            //Log.e("HomeActivity", "Error updating list for directory.");
            return;
        }

        File[] fileList = dir.listFiles();

        for (int i = 0; i < fileList.length; i++)
        {
            if (fileList[i].isDirectory())
            {
                directoryPathList.add(fileList[i].getAbsolutePath());
            }
            else
            if (fileList[i].getAbsolutePath().indexOf(".lumn") > 0)
            {
                filePathList.add(fileList[i].getAbsolutePath());
            }
        }

        ((TextView) findViewById(R.id.main_directory_list_label)).setText(currentPath.substring(currentPath.indexOf(HOME_DIRECTORY_NAME)));
    }

    private void generateFirstPatternFiles()
    {
        File homeDir = new File(getFilesDir(), HOME_DIRECTORY_NAME);
        if (homeDir.exists())
        {
            //Log.i("MainActivity", "Home directory exists.");
            // App created home directory when it ran for the first time, so no need to do anything.
            //deleteFiles(homeDir);
            return;
        }

        // First run, so create home directory and generate pre-made pattern files

        homeDir.mkdir();
        File currentDir = homeDir;

        AssetManager am = App.getApplication().getAssets();
        InputStream patternStream = null;
        Scanner s = null;


        try
        {
            patternStream = am.open("firstPatterns.txt");
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "Could not open initial asset file of patterns.");
            return;
        }

        if (patternStream != null)
        {
            s = new Scanner(patternStream);
        }

        while (s.hasNextLine())
        {
            try
            {
                String line = s.nextLine();
                String patternFilename = getCurrentDateAsString() + ".lumn";
                File patternFile = new File(currentDir, patternFilename);

                if (line.charAt(0) == '{')
                {
                    PrintWriter out = new PrintWriter(patternFile);
                    out.write(line);
                    out.close();
                }
                else
                if (!line.isEmpty())
                {
                    currentDir = new File(homeDir, line);
                    currentDir.mkdir();
                }
            }
            catch (IOException e)
            {
                Log.e("MainActivity", e.getMessage());
            }
        }


    }

    private void deleteFiles(File dir)
    {
        for (File f : dir.listFiles())
        {
            if (f.isDirectory())
            {
                deleteFiles(f);
            }
            else
            {
                f.delete();
            }
        }

        dir.delete();
    }

    private void createPatternFile(String patternName)
    {
        Pattern newPattern = new Pattern();
        newPattern.setName(patternName);

        Calendar calendar = Calendar.getInstance();

        String filename = getCurrentDateAsString() + ".lumn";
        String jsonString = newPattern.toJSONString();

        try {
            File patternFile = new File(currentPath, filename);
            FileOutputStream out = new FileOutputStream(patternFile);
            out.write(jsonString.getBytes());
            out.close();
        } catch (IOException e) {
            //Log.e("HomeActivity", e.getMessage());
        }

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Pattern \"" + patternName + "\" created at the end of the pattern list.", Snackbar.LENGTH_LONG);
    }

    private void movePatternFile(String patternFilename, String directoryFilename)
    {
        if (!directoryFilename.contains(HOME_DIRECTORY_NAME))
        {
            SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Don't try to smuggle patterns up there!", Snackbar.LENGTH_LONG);
            return;
        }

        File patternFile = new File(patternFilename);
        String newPatternFilename = System.currentTimeMillis() + ".lumn";
        File directory = new File(directoryFilename);
        File newPatternFile = new File(directoryFilename, newPatternFilename);

        try
        {
            newPatternFile.createNewFile();

            FileInputStream in = new FileInputStream(patternFile);
            FileOutputStream out = new FileOutputStream(newPatternFile);

            int ch = 0;
            while ((ch = in.read()) != -1)
            {
                out.write((char) ch);
            }

            in.close();
            out.close();

            patternFile.delete();
        }
        catch (IOException e)
        {
            SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Could not move pattern.", Snackbar.LENGTH_LONG);
            //Log.e("HomeActivity", e.getMessage());

            return;
        }

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Pattern moved to \"" + directory.getName() + "\".", Snackbar.LENGTH_LONG);
    }

    private void duplicatePatternFile(String patternFilename)
    {
        File patternFile = new File(patternFilename);
        String newPatternFilename = System.currentTimeMillis() + ".lumn";
        File newPatternFile = new File(currentPath, newPatternFilename);

        try
        {
            FileInputStream in = new FileInputStream(patternFile);
            FileOutputStream out = new FileOutputStream(newPatternFile);

            int ch = 0;
            while ((ch = in.read()) != -1)
            {
                out.write((char) ch);
            }

            in.close();
            out.close();
        }
        catch (IOException e)
        {
            SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Could not duplicate pattern.", Snackbar.LENGTH_LONG);
            //Log.e("HomeActivity", e.getMessage());

            return;
        }

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Pattern duplicated.", Snackbar.LENGTH_LONG);
    }

    private void deletePatternFile(String patternFilename)
    {
        File patternFile = new File(patternFilename);
        patternFile.delete();

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Pattern deleted.", Snackbar.LENGTH_LONG);
    }

    private void createDirectory(String directoryName)
    {
        File newDirectory = new File(currentPath, directoryName);

        if (newDirectory.exists() && newDirectory.isDirectory())
        {
            SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Folder \"" + directoryName + "\" already exists.", Snackbar.LENGTH_LONG);
            return;
        }

        newDirectory.mkdir();

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Folder \"" + directoryName + "\" created at the end of the folder list.", Snackbar.LENGTH_LONG);
    }

    private void renameDirectory(String oldDirectoryName, String newDirectoryName)
    {
        File oldDirectory = new File(currentPath, oldDirectoryName);
        File newDirectory = new File(currentPath, newDirectoryName);
        oldDirectory.renameTo(newDirectory);

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Folder \"" + oldDirectoryName + "\" renamed to \"" + newDirectoryName + "\".", Snackbar.LENGTH_LONG);
    }

    private void deleteDirectory(String directoryName)
    {
        File directory = new File(currentPath, directoryName);
        deleteFiles(directory);

        updateListForDirectory(new File(currentPath), patternFilePathList, directoryFilePathList);
        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();

        SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Folder \"" + directoryName + "\" deleted.", Snackbar.LENGTH_LONG);
    }

    @Override
    public void onPatternPlayButtonClick(String patternFilepath)
    {
        Intent glTestIntent = new Intent(MainActivity.this, GLTestActivity.class);
        glTestIntent.putExtra("patternFilepath", patternFilepath);
        //Log.i("HomeActivity", patternFilepath);
        startActivity(glTestIntent);
    }

    @Override
    public void onPatternEditButtonClick(String patternFilepath) {
        Intent editorIntent = new Intent(MainActivity.this, PatternEditorActivity.class);
        editorIntent.putExtra("patternFilepath", patternFilepath);
        //Log.i("HomeActivity", patternFilepath);
        startActivity(editorIntent);
    }

    @Override
    public void onPatternListItemLongClick(String patternFilepath)
    {
        invokePatternOptionsDialog(PATTERN_OPTIONS, patternFilepath);
    }

    @Override
    public void onDirectoryImageClick(String directoryFilepath)
    {
        if (!directoryFilepath.contains(HOME_DIRECTORY_NAME))
        {
            SnackbarFactory.showSnackbar(findViewById(R.id.home_nested_scroll_view), "Nothing to see up there!", Snackbar.LENGTH_LONG);
            return;
        }

        File directory = new File(directoryFilepath);
        currentPath = directoryFilepath;
        updateListForDirectory(directory, patternFilePathList, directoryFilePathList);

        directoryRVAdapter.notifyDataSetChanged();
        patternRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDirectoryImageLongClick(String directoryFilepath)
    {
        File directory = new File(directoryFilepath);
        invokeDirectoryOptionsDialog(DIRECTORY_OPTIONS + ": " + directory.getName());
    }

    @Override
    public void invokeEditTextDialog(String inputPrompt, String defaultText)
    {
        EditTextDialogFragment editTextDialogFragment = EditTextDialogFragment.newInstance(inputPrompt, defaultText, this);
        editTextDialogFragment.show(getSupportFragmentManager(), "editTextDialogFragment");
    }

    @Override
    public void onConfirmEditText(String inputPrompt, String inputText)
    {
        if (inputPrompt.equals(NEW_PATTERN_NAME))
        {
            createPatternFile(inputText);
        }
        else
        if (inputPrompt.equals(NEW_DIRECTORY_NAME))
        {
            createDirectory(inputText);
        }
        else
        if (inputPrompt.contains(RENAME_DIRECTORY))
        {
            renameDirectory(inputPrompt.substring(inputPrompt.indexOf(":") + 2), inputText);
        }
    }

    @Override
    public void invokePatternOptionsDialog(String inputPrompt, String patternFilename)
    {
        PatternOptionsDialog newPatternOptionsDialog = PatternOptionsDialog.newInstance(inputPrompt, patternFilename, this);
        newPatternOptionsDialog.show(getSupportFragmentManager(), "newPatternOptionsDialog");
    }

    @Override
    public void onConfirmPatternOption(String inputPrompt, final String patternFilename, int patternOption)
    {
        if (patternOption == PatternOptionsDialog.MOVE_PATTERN)
        {
            invokeMovePatternDialog(MOVE_PATTERN, patternFilename);
        }
        else
        if (patternOption == PatternOptionsDialog.DUPLICATE_PATTERN)
        {
            duplicatePatternFile(patternFilename);
        }
        else
        if (patternOption == PatternOptionsDialog.DELETE_PATTERN)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to delete this pattern?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            deletePatternFile(patternFilename);
                            //Log.i("PatternEditorActivity", "Saved before exiting!");
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // do nothing
                        }
                    })
                    .setCancelable(true);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void invokeMovePatternDialog(String inputPrompt, String patternFilename)
    {
        MovePatternDialog newMovePatternDialog = MovePatternDialog.newInstance(inputPrompt, patternFilename, directoryFilePathList, this);
        newMovePatternDialog.show(getSupportFragmentManager(), "newMovePatternDialog");
    }

    @Override
    public void onConfirmMovePatternOption(String inputPrompt, String patternFilename, String directoryName)
    {
        movePatternFile(patternFilename, directoryName);
    }

    @Override
    public void invokePatternDirectoryDialog(String inputPrompt)
    {
        NewPatternDirectoryDialog newPatternDirectoryDialog = NewPatternDirectoryDialog.newInstance(inputPrompt, this);
        newPatternDirectoryDialog.show(getSupportFragmentManager(), "newPatternDirectoryDialog");
    }

    @Override
    public void onConfirmPatternDirectory(String inputPrompt, int patternDirectory)
    {
        if (patternDirectory == NewPatternDirectoryDialog.NEW_PATTERN)
        {
            invokeEditTextDialog(NEW_PATTERN_NAME, "My New Pattern");
        }
        else
        if (patternDirectory == NewPatternDirectoryDialog.NEW_DIRECTORY)
        {
            invokeEditTextDialog(NEW_DIRECTORY_NAME, "New Directory");
        }
    }

    @Override
    public void invokeDirectoryOptionsDialog(String inputPrompt)
    {
        DirectoryOptionsDialog newDirectoryOptionsDialog = DirectoryOptionsDialog.newInstance(inputPrompt, this);
        newDirectoryOptionsDialog.show(getSupportFragmentManager(), "newDirectoryOptionsDialog");
    }

    @Override
    public void onConfirmDirectoryOption(String inputPrompt, int directoryOption)
    {
        final String directoryName = inputPrompt.substring(inputPrompt.indexOf(':') + 2);

        if (directoryOption == DirectoryOptionsDialog.RENAME_DIRECTORY)
        {
            invokeEditTextDialog(RENAME_DIRECTORY + ": " + directoryName, directoryName);
        }
        else
        if (directoryOption == DirectoryOptionsDialog.DELETE_DIRECTORY)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Are you sure you want to delete folder \"" + directoryName + "\"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            deleteDirectory(directoryName);
                            //Log.i("PatternEditorActivity", "Saved before exiting!");
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            // do nothing
                        }
                    })
                    .setCancelable(true);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private String getCurrentDateAsString()
    {
        Calendar calendar = Calendar.getInstance();

        return String.format("%04d", calendar.get(Calendar.YEAR)) + "-" +
                String.format("%02d", calendar.get(Calendar.MONTH)) + "-" +
                String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)) + "-" +
                String.format("%02d", calendar.get(Calendar.HOUR)) + "-" +
                String.format("%02d", calendar.get(Calendar.MINUTE)) + "-" +
                String.format("%02d", calendar.get(Calendar.SECOND)) + "-" +
                String.format("%03d", calendar.get(Calendar.MILLISECOND));
    }
}
