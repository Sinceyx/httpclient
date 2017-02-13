package com.other.httpclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpResponse;

public class NettyClientHandler extends SimpleChannelInboundHandler<HttpResponse>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpResponse msg) throws Exception {
		// TODO Auto-generated method stub
		System.out.println(msg.getStatus().toString());
		
	}



}
