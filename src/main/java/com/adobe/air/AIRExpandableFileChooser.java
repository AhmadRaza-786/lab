package com.adobe.air;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.adobe.air.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIRExpandableFileChooser implements ExpandableListView.OnChildClickListener {
    private static final String FILEINFO = "FILEINFO";
    public static final String TAG = AIRExpandableFileChooser.class.toString();
    private static final String TYPE = "TYPE";
    private static final String USER_ACTION_CANCEL = "cancel";
    private static final String USER_ACTION_DONE = "done";
    /* access modifiers changed from: private */
    public ExpandableListAdapter mAdapter;
    /* access modifiers changed from: private */
    public boolean mAllowMultiple = false;
    /* access modifiers changed from: private */
    public SparseBooleanArray mCheckedFiles = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public List<List<Map<String, FileInfo>>> mChildData = new ArrayList();
    /* access modifiers changed from: private */
    public AndroidAlertDialog mFileChooserDialog = null;
    /* access modifiers changed from: private */
    public FileChooserStub mFileChooserStub = null;
    private final String[] mFileProjection = {"_data", "_display_name"};
    /* access modifiers changed from: private */
    public EditText mFileSaveName = null;
    /* access modifiers changed from: private */
    public ArrayList<String> mFilenames = new ArrayList<>();
    /* access modifiers changed from: private */
    public List<Map<String, String>> mGroupData = new ArrayList();
    private View mMultipleFileSelectionView = null;
    private LayoutInflater mRuntimeInflater = null;
    private Resources mRuntimeResources = null;
    private boolean mSave = false;
    private String mSelection = new String();

    private class FileInfo {
        public String mFileName = new String();
        public String mFilePath = new String();
        public FileChooserItem mParent = null;

        public FileInfo(String str, String str2) {
            this.mFileName = str;
            this.mFilePath = str2;
        }
    }

    private class FileChooserItem extends LinearLayout {
        private CompoundButton mFileCheckBox;
        private TextView mFileNameView;
        private TextView mFilePathView;
        /* access modifiers changed from: private */
        public int mListFlatPosition = -1;
        private CompoundButton.OnCheckedChangeListener mListener;

        FileChooserItem(LayoutInflater layoutInflater, Resources resources, ViewGroup viewGroup, int i, int i2) {
            super(AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity());
            if (AIRExpandableFileChooser.this.mAllowMultiple) {
                View GetLayoutViewFromRuntime = Utils.GetLayoutViewFromRuntime("expandable_multiple_chooser_row", resources, layoutInflater);
                Resources resources2 = GetLayoutViewFromRuntime.getResources();
                this.mFileNameView = (TextView) Utils.GetWidgetInViewByName("filename", resources2, GetLayoutViewFromRuntime);
                this.mFilePathView = (TextView) Utils.GetWidgetInViewByName("filepath", resources2, GetLayoutViewFromRuntime);
                this.mFileCheckBox = (CompoundButton) Utils.GetWidgetInViewByName("filecheck", resources2, GetLayoutViewFromRuntime);
                if (this.mFileNameView == null || this.mFilePathView == null || this.mFileCheckBox == null) {
                }
                this.mListFlatPosition = AIRExpandableFileChooser.this.expandableListPositionToFlatPosition(i, i2);
                this.mListener = new CompoundButton.OnCheckedChangeListener(AIRExpandableFileChooser.this) {
                    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                        if (FileChooserItem.this.mListFlatPosition >= 0) {
                            if (z) {
                                AIRExpandableFileChooser.this.mCheckedFiles.put(FileChooserItem.this.mListFlatPosition, z);
                            } else {
                                AIRExpandableFileChooser.this.mCheckedFiles.delete(FileChooserItem.this.mListFlatPosition);
                            }
                        }
                    }
                };
                addView(GetLayoutViewFromRuntime);
                return;
            }
            View GetLayoutViewFromRuntime2 = Utils.GetLayoutViewFromRuntime("expandable_chooser_row", resources, layoutInflater);
            Resources resources3 = GetLayoutViewFromRuntime2.getResources();
            this.mFileNameView = (TextView) Utils.GetWidgetInViewByName("filename", resources3, GetLayoutViewFromRuntime2);
            this.mFilePathView = (TextView) Utils.GetWidgetInViewByName("filepath", resources3, GetLayoutViewFromRuntime2);
            if (this.mFileNameView == null || this.mFilePathView == null) {
            }
            addView(GetLayoutViewFromRuntime2);
        }

        public void bindToData(FileInfo fileInfo, int i, int i2) {
            this.mFileNameView.setText(fileInfo.mFileName);
            this.mFilePathView.setText(fileInfo.mFilePath);
            if (AIRExpandableFileChooser.this.mAllowMultiple) {
                this.mListFlatPosition = AIRExpandableFileChooser.this.expandableListPositionToFlatPosition(i, i2);
                this.mFileCheckBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                this.mFileCheckBox.setChecked(AIRExpandableFileChooser.this.mCheckedFiles.get(this.mListFlatPosition));
                this.mFileCheckBox.setOnCheckedChangeListener(this.mListener);
                fileInfo.mParent = this;
            }
        }

        public void toggle() {
            if (AIRExpandableFileChooser.this.mAllowMultiple) {
                this.mListener.onCheckedChanged(this.mFileCheckBox, !this.mFileCheckBox.isChecked());
                this.mFileCheckBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                this.mFileCheckBox.setChecked(AIRExpandableFileChooser.this.mCheckedFiles.get(this.mListFlatPosition));
                this.mFileCheckBox.setOnCheckedChangeListener(this.mListener);
            }
        }

        public void uncheck() {
            if (AIRExpandableFileChooser.this.mAllowMultiple) {
                this.mListener.onCheckedChanged(this.mFileCheckBox, false);
                this.mFileCheckBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
                this.mFileCheckBox.setChecked(false);
                this.mFileCheckBox.setOnCheckedChangeListener(this.mListener);
            }
        }
    }

    private class FileChooserExpandableListAdapter extends BaseExpandableListAdapter {
        private LayoutInflater mRuntimeInflater = null;
        private Resources mRuntimeResources = null;

        public FileChooserExpandableListAdapter(LayoutInflater layoutInflater, Resources resources) {
            this.mRuntimeInflater = layoutInflater;
            this.mRuntimeResources = resources;
        }

        public Object getChild(int i, int i2) {
            return ((List) AIRExpandableFileChooser.this.mChildData.get(i)).get(i2);
        }

        public long getChildId(int i, int i2) {
            return (long) i2;
        }

        public View getChildView(int i, int i2, boolean z, View view, ViewGroup viewGroup) {
            FileChooserItem fileChooserItem;
            if (view == null || !(view instanceof FileChooserItem)) {
                fileChooserItem = new FileChooserItem(this.mRuntimeInflater, this.mRuntimeResources, viewGroup, i, i2);
            } else {
                fileChooserItem = (FileChooserItem) view;
            }
            fileChooserItem.bindToData((FileInfo) ((Map) ((List) AIRExpandableFileChooser.this.mChildData.get(i)).get(i2)).get(AIRExpandableFileChooser.FILEINFO), i, i2);
            return fileChooserItem;
        }

        public int getChildrenCount(int i) {
            return ((List) AIRExpandableFileChooser.this.mChildData.get(i)).size();
        }

        public Object getGroup(int i) {
            return AIRExpandableFileChooser.this.mGroupData.get(i);
        }

        public int getGroupCount() {
            return AIRExpandableFileChooser.this.mGroupData.size();
        }

        public long getGroupId(int i) {
            return (long) i;
        }

        public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.mRuntimeInflater.inflate(17367046, viewGroup, false);
            }
            TextView textView = (TextView) view.findViewById(16908308);
            if (textView != null) {
                textView.setText((CharSequence) ((Map) AIRExpandableFileChooser.this.mGroupData.get(i)).get(AIRExpandableFileChooser.TYPE));
            }
            return view;
        }

        public boolean isChildSelectable(int i, int i2) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
    }

    public AIRExpandableFileChooser(ArrayList<String> arrayList, boolean z, boolean z2, String str, FileChooserStub fileChooserStub) {
        this.mSave = z;
        this.mAllowMultiple = z2;
        this.mFileChooserStub = fileChooserStub;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            arrayList.add("*");
        }
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            String str2 = arrayList.get(i);
            if (str2.startsWith("*")) {
                str2 = str2.substring(1);
                if (str2.length() == 0) {
                    break;
                }
            }
            if (this.mSelection.length() != 0) {
                this.mSelection += " OR ";
            }
            this.mSelection += "_display_name LIKE '%" + str2 + "'";
        }
        AndroidActivityWrapper GetAndroidActivityWrapper = AndroidActivityWrapper.GetAndroidActivityWrapper();
        this.mFileChooserDialog = new AndroidAlertDialog(GetAndroidActivityWrapper.getActivity());
        AlertDialog.Builder GetAlertDialogBuilder = this.mFileChooserDialog.GetAlertDialogBuilder();
        GetAlertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() != 4) {
                    return false;
                }
                AIRExpandableFileChooser.this.mFileChooserStub.SetUserAction(AIRExpandableFileChooser.USER_ACTION_CANCEL);
                return false;
            }
        });
        Context runtimeContext = GetAndroidActivityWrapper.getRuntimeContext();
        this.mRuntimeInflater = LayoutInflater.from(runtimeContext);
        this.mRuntimeResources = runtimeContext.getResources();
        View GetLayoutViewFromRuntime = Utils.GetLayoutViewFromRuntime("main", this.mRuntimeResources, this.mRuntimeInflater);
        GetAlertDialogBuilder.setView(GetLayoutViewFromRuntime);
        Resources resources = GetLayoutViewFromRuntime.getResources();
        ExpandableListView expandableListView = (ExpandableListView) Utils.GetWidgetInViewByName("list", resources, GetLayoutViewFromRuntime);
        expandableListView.setOnChildClickListener(this);
        expandableListView.setEmptyView(GetAndroidActivityWrapper.getActivity().findViewById(16908292));
        RelativeLayout relativeLayout = (RelativeLayout) Utils.GetWidgetInViewByName("file_save_panel", resources, GetLayoutViewFromRuntime);
        if (this.mSave) {
            GetAlertDialogBuilder.setTitle(Utils.GetResourceStringFromRuntime("file_download", this.mRuntimeResources));
            relativeLayout.setVisibility(0);
            this.mFileSaveName = (EditText) Utils.GetWidgetInViewByName("file_save_name", resources, GetLayoutViewFromRuntime);
            if (str != null) {
                this.mFileSaveName.setText(Utils.GetExternalStorageDirectory() + "/" + str);
            }
            this.mFileSaveName.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() != 0 || i != 66) {
                        return false;
                    }
                    String obj = AIRExpandableFileChooser.this.mFileSaveName.getText().toString();
                    if (obj.length() != 0) {
                        AIRExpandableFileChooser.this.mFilenames.clear();
                        AIRExpandableFileChooser.this.mFilenames.add(obj);
                        AIRExpandableFileChooser.this.mFileChooserStub.SetUserAction(AIRExpandableFileChooser.USER_ACTION_DONE);
                        AIRExpandableFileChooser.this.HideVirtualKeyboard(AIRExpandableFileChooser.this.mFileSaveName);
                        AIRExpandableFileChooser.this.mFileChooserDialog.dismiss();
                    }
                    return true;
                }
            });
            ((Button) Utils.GetWidgetInViewByName("file_save_button", resources, GetLayoutViewFromRuntime)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String obj = AIRExpandableFileChooser.this.mFileSaveName.getText().toString();
                    if (obj.length() != 0) {
                        AIRExpandableFileChooser.this.mFilenames.clear();
                        AIRExpandableFileChooser.this.mFilenames.add(obj);
                        AIRExpandableFileChooser.this.mFileChooserStub.SetUserAction(AIRExpandableFileChooser.USER_ACTION_DONE);
                        AIRExpandableFileChooser.this.HideVirtualKeyboard(AIRExpandableFileChooser.this.mFileSaveName);
                        AIRExpandableFileChooser.this.mFileChooserDialog.dismiss();
                    }
                }
            });
        } else {
            GetAlertDialogBuilder.setTitle(Utils.GetResourceStringFromRuntime("file_upload", this.mRuntimeResources));
            relativeLayout.setVisibility(8);
        }
        AddMediaSubtree(Utils.GetResourceStringFromRuntime("audio_files", this.mRuntimeResources), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        AddMediaSubtree(Utils.GetResourceStringFromRuntime("image_files", this.mRuntimeResources), MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        AddMediaSubtree(Utils.GetResourceStringFromRuntime("video_files", this.mRuntimeResources), MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        if (!this.mGroupData.isEmpty()) {
            this.mAdapter = new FileChooserExpandableListAdapter(this.mRuntimeInflater, this.mRuntimeResources);
            expandableListView.setAdapter(this.mAdapter);
            expandableListView.setItemsCanFocus(true);
            ((TextView) Utils.GetWidgetInViewByName("empty", resources, GetLayoutViewFromRuntime)).setVisibility(8);
        } else {
            expandableListView.setVisibility(8);
        }
        if (this.mAllowMultiple) {
            createInvisibleMultipleFileSelectionView();
        }
    }

    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long j) {
        if (!(view instanceof FileChooserItem)) {
            return false;
        }
        FileChooserItem fileChooserItem = (FileChooserItem) view;
        if (!this.mAllowMultiple) {
            HashMap hashMap = (HashMap) this.mAdapter.getChild(i, i2);
            if (this.mSave) {
                this.mFileSaveName.setText(((FileInfo) hashMap.get(FILEINFO)).mFilePath);
            } else {
                this.mFilenames.clear();
                this.mFilenames.add(((FileInfo) hashMap.get(FILEINFO)).mFilePath);
                this.mFileChooserStub.SetUserAction(USER_ACTION_DONE);
                this.mFileChooserDialog.dismiss();
            }
        } else {
            fileChooserItem.toggle();
        }
        return true;
    }

    /* access modifiers changed from: private */
    public int expandableListPositionToFlatPosition(int i, int i2) {
        int i3 = 0;
        if (i < 0 || i >= this.mChildData.size()) {
            return -1;
        }
        if (i2 < 0 || i2 >= this.mChildData.get(i).size()) {
            return -1;
        }
        for (int i4 = 0; i4 < i; i4++) {
            i3 += this.mChildData.get(i4).size();
        }
        return i3 + i2;
    }

    /* access modifiers changed from: private */
    public void uncheckAll() {
        if (this.mAllowMultiple) {
            int size = this.mGroupData.size();
            for (int i = 0; i < size; i++) {
                int size2 = this.mChildData.get(i).size();
                for (int i2 = 0; i2 < size2; i2++) {
                    FileChooserItem fileChooserItem = ((FileInfo) ((HashMap) this.mAdapter.getChild(i, i2)).get(FILEINFO)).mParent;
                    if (fileChooserItem != null) {
                        fileChooserItem.uncheck();
                    }
                }
            }
        }
    }

    private void createInvisibleMultipleFileSelectionView() {
        AlertDialog.Builder GetAlertDialogBuilder = this.mFileChooserDialog.GetAlertDialogBuilder();
        GetAlertDialogBuilder.setPositiveButton(Utils.GetResourceStringFromRuntime("button_ok", this.mRuntimeResources), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AIRExpandableFileChooser.this.mFilenames.clear();
                int size = AIRExpandableFileChooser.this.mGroupData.size();
                for (int i2 = 0; i2 < size; i2++) {
                    int size2 = ((List) AIRExpandableFileChooser.this.mChildData.get(i2)).size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        if (AIRExpandableFileChooser.this.mCheckedFiles.get(AIRExpandableFileChooser.this.expandableListPositionToFlatPosition(i2, i3))) {
                            AIRExpandableFileChooser.this.mFilenames.add(((FileInfo) ((HashMap) AIRExpandableFileChooser.this.mAdapter.getChild(i2, i3)).get(AIRExpandableFileChooser.FILEINFO)).mFilePath);
                        }
                    }
                }
                AIRExpandableFileChooser.this.mFileChooserStub.SetUserAction(AIRExpandableFileChooser.USER_ACTION_DONE);
            }
        });
        GetAlertDialogBuilder.setNegativeButton(Utils.GetResourceStringFromRuntime("button_cancel", this.mRuntimeResources), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                AIRExpandableFileChooser.this.uncheckAll();
                AIRExpandableFileChooser.this.mFileChooserStub.SetUserAction(AIRExpandableFileChooser.USER_ACTION_CANCEL);
            }
        });
    }

    private void AddMediaSubtree(String str, Uri uri) {
        Cursor managedQuery = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().managedQuery(uri, this.mFileProjection, this.mSelection.length() == 0 ? null : this.mSelection, (String[]) null, (String) null);
        ArrayList arrayList = new ArrayList();
        if (managedQuery != null && managedQuery.moveToFirst()) {
            HashMap hashMap = new HashMap();
            hashMap.put(TYPE, str);
            this.mGroupData.add(hashMap);
            do {
                FileInfo fileInfo = new FileInfo(managedQuery.getString(managedQuery.getColumnIndex("_display_name")), managedQuery.getString(managedQuery.getColumnIndex("_data")));
                HashMap hashMap2 = new HashMap();
                arrayList.add(hashMap2);
                hashMap2.put(FILEINFO, fileInfo);
            } while (managedQuery.moveToNext());
            this.mChildData.add(arrayList);
        }
    }

    /* access modifiers changed from: private */
    public void HideVirtualKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public AndroidAlertDialog GetDialog() {
        return this.mFileChooserDialog;
    }

    public ArrayList<String> GetFileNames() {
        return this.mFilenames;
    }
}
