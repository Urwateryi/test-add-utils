package com.rato.tianyi.basic.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rato.tianyi.R;
import com.rato.tianyi.basic.utils.SociaxUtil;
import com.rato.tianyi.basic.utils.TxtUtil;

import java.util.Timer;
import java.util.TimerTask;

import static framework.util.RegularUtils.emojiFilters;

/**
 * Time: 2016/10/20 0012 15:15
 */
public class SearchComponent extends LinearLayout implements EdtInterface, View.OnKeyListener {

    private ImageView iv_del;
    private ImageView iv_back;
    private EditText et_content;
    private TextView tv_search;
    private Context mContext;
    private boolean isShowKeyBoard = false;//是否默认显示软键盘
    private boolean isShowBackAndSearchButton = false;//是否默认显示返回按钮和搜索按钮
    private String hintTxt = "";//缺省文字，非公共布局会用到此属性

    public SearchComponent(Context context) {
        super(context);
    }

    public SearchComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.widget_search_et_btn, this, true);
        initView();
    }

    /**
     * 初始化属性
     *
     * @param attrs
     */
    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SearchComponent);
        isShowKeyBoard = typedArray.getBoolean(R.styleable.SearchComponent_isShowKeyBoard, false);
        isShowBackAndSearchButton = typedArray.getBoolean(R.styleable.SearchComponent_isShowBackAndSearchButton, false);
        hintTxt = typedArray.getString(R.styleable.SearchComponent_hint);
        typedArray.recycle();
    }

    /**
     * 公共布局会用到此属性
     *
     * @param str
     * @return
     */
    public SearchComponent setHint(String str) {
        et_content.setHint(str);
        return this;
    }

    private void initView() {
        iv_del = (ImageView) findViewById(R.id.iv_del_content);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_search = (TextView) findViewById(R.id.tv_search);
        et_content = (EditText) findViewById(R.id.et_content_search);
        if (isShowBackAndSearchButton) {
            iv_back.setVisibility(View.VISIBLE);
            tv_search.setVisibility(View.VISIBLE);
        } else {
            iv_back.setVisibility(View.GONE);
            tv_search.setVisibility(View.GONE);
        }
        if (!TxtUtil.isEmpty(hintTxt)) {
            et_content.setHint(hintTxt);
        }
        if (isShowKeyBoard) {
            et_content.setFocusable(true);
            et_content.setFocusableInTouchMode(true);
            et_content.requestFocus();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                               public void run() {
                                   InputMethodManager inputManager = (InputMethodManager) et_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                   inputManager.showSoftInput(et_content, 0);
                               }
                           },
                    600);
        } else {
            et_content.clearFocus();
        }
        et_content.setFilters(emojiFilters);
        et_content.addTextChangedListener(tw);// 为输入框绑定一个监听文字变化的监听器
        // 添加按钮点击事件
        iv_del.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBtn();// 隐藏按钮
                et_content.setText("");// 设置输入框内容为空
                mListener.delAll();
            }
        });

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        });

        tv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.search(et_content.getText().toString().trim());
            }
        });

        et_content.setOnKeyListener(this);
    }

    public String getText() {
        return et_content.getText().toString();
    }

    public Editable getEditable() {
        return et_content.getText();
    }

    public void setText(String content) {
        et_content.setText(content);
    }

    // 当输入框状态改变时，会调用相应的方法
    TextWatcher tw = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        // 在文字改变后调用
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                hideBtn();// 隐藏按钮
            } else {
                showBtn();// 显示按钮
            }
            if (mListener != null) {
                mListener.onTextChanged(s, s.length());
            }
        }
    };

    @Override
    public void hideBtn() {
        // 设置按钮不可见
        if (iv_del.isShown()) {
            iv_del.setVisibility(View.GONE);
        }
    }

    @Override
    public void showBtn() {
        // 设置按钮可见
        if (!iv_del.isShown()) {
            iv_del.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event != null
                && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_ENVELOPE)
                && event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_UP) {

            mListener.search(et_content.getText().toString().trim());

            return true;
        }
        return false;
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyBoard() {
        SociaxUtil.hideSoftKeyboard(mContext, et_content);
    }

    public interface onEventLisener {
        void delAll();//全部删除

        void onTextChanged(Editable s, int length);

        void search(String content);
    }

    private onEventLisener mListener;

    public void setOnEventLisener(onEventLisener lisener) {
        mListener = lisener;
    }
}