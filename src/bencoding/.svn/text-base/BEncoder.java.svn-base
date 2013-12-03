/* BDecoder - Converts an InputStream to BEValues.
   Copyright (C) 2003 Mark J. Wielaard

   This file is part of Snark.
   
   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2, or (at your option)
   any later version.
 
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
 
   You should have received a copy of the GNU General Public License
   along with this program; if not, write to the Free Software Foundation,
   Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

   In addition, as a special exception, the copyright holders of Snark give
   you permission to combine Snark with free software programs or libraries
   that are released under the GNU LGPL and with any code released under
   the Apache Software License, version 1.0, 1.1 or 2.0. You may copy and
   distribute such a system following the terms of the GNU GPL for Snark
   and the following licenses of the other code concerned, provided that
   you include the source code of that other code when and as the GNU GPL
   requires distribution of source code.

   Note that people who make modified versions of Snark are not
   obligated to grant this special exception for their modified versions;
   it is their choice whether to do so. The GNU General Public License
   gives permission to release a modified version without this exception;
   this exception also makes it possible to release a modified version
   which carries forward this exception.
 */

package bencoding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BEncoder {

	public static byte[] bencode(Object o) throws IllegalArgumentException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bencode(o, baos);
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new InternalError(ioe.toString());
		}
	}

	@SuppressWarnings("rawtypes")
	public static void bencode(Object o, OutputStream out) throws IOException,
			IllegalArgumentException {
		if (o instanceof String)
			bencode((String) o, out);
		else if (o instanceof byte[])
			bencode((byte[]) o, out);
		else if (o instanceof Number)
			bencode((Number) o, out);
		else if (o instanceof List)
			bencode((List) o, out);
		else if (o instanceof Map)
			bencode((Map) o, out);
		else
			throw new IllegalArgumentException("Cannot bencode: "
					+ o.getClass());
	}
	
	public static byte[] bencode(String s) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bencode(s, baos);
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new InternalError(ioe.toString());
		}
	}

	public static void bencode(String s, OutputStream out) throws IOException {
		byte[] bs = s.getBytes("UTF-8");
		bencode(bs, out);
	}

	public static byte[] bencode(Number n) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bencode(n, baos);
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new InternalError(ioe.toString());
		}
	}

	public static void bencode(Number n, OutputStream out) throws IOException {
		out.write('i');
		String s = n.toString();
		out.write(s.getBytes("UTF-8"));
		out.write('e');
	}

	@SuppressWarnings("rawtypes")
	public static byte[] bencode(List l) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bencode(l, baos);
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new InternalError(ioe.toString());
		}
	}

	@SuppressWarnings("rawtypes")
	public static void bencode(List l, OutputStream out) throws IOException {
		out.write('l');
		Iterator it = l.iterator();
		while (it.hasNext())
			bencode(it.next(), out);
		out.write('e');
	}

	public static byte[] bencode(byte[] bs) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bencode(bs, baos);
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new InternalError(ioe.toString());
		}
	}

	public static void bencode(byte[] bs, OutputStream out) throws IOException {
		String l = Integer.toString(bs.length);
		out.write(l.getBytes("UTF-8"));
		out.write(':');
		out.write(bs);
	}

	@SuppressWarnings("rawtypes")
	public static byte[] bencode(Map m) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bencode(m, baos);
			return baos.toByteArray();
		} catch (IOException ioe) {
			throw new InternalError(ioe.toString());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void bencode(Map m, OutputStream out) throws IOException {
		out.write('d');

		// Keys must be sorted. XXX - But is this the correct order?
		Set s = m.keySet();
		List l = new ArrayList(s);
		Collections.sort(l);

		Iterator it = l.iterator();
		while (it.hasNext()) {
			// Keys must be Strings.
			String key = (String) it.next();
			Object value = m.get(key);
			bencode(key, out);
			bencode(value, out);
		}

		out.write('e');
	}
}
