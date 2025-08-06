package com.xksms.log.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @Copyright (C)
 * @Author: 姿势帝
 * @Date: 6/3 16:37
 * @Description:
 */
public class IpConfig extends ClassicConverter {
	@Override
	public String convert(ILoggingEvent iLoggingEvent) {
		InetAddress addr = null;
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();

			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress ipTmp = addresses.nextElement();
					if (ipTmp instanceof Inet4Address
							&& ipTmp.isSiteLocalAddress()
							&& !ipTmp.isLoopbackAddress()
							&& !ipTmp.getHostAddress().contains(":")) {
						addr = ipTmp;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return addr.getHostAddress();
	}
}