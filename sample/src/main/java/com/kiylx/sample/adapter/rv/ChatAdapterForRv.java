package com.kiylx.sample.adapter.rv;

import android.content.Context;

import com.kiylx.adapter.recyclerview.MultiItemTypeAdapter;
import com.kiylx.sample.bean.ChatMessage;

import java.util.List;

/**
 * Created by zhy on 15/9/4.
 */
public class ChatAdapterForRv extends MultiItemTypeAdapter<ChatMessage>
{
    public ChatAdapterForRv(Context context, List<ChatMessage> datas)
    {
        super(context, datas);

        addItemViewDelegate(new MsgSendItemDelagate());
        addItemViewDelegate(new MsgComingItemDelagate());
    }
}
