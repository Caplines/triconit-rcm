package com.tricon.esdatareplication.logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.CustomPropFileCache;

@Component
public class AppLogger {

	public Object[] createNewLogFile() {

		BufferedWriter bw = null;
		FileWriter fw = null;
		try {

			File file = new File(CustomPropFileCache.cache.get(Constants.CACHE_NAME_FOR_PROP).getLogLocation() + "/"
					+ Constants.SDF_LOG_FILE.format(new Date()) + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			// true = append file
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);

		} catch (IOException e) {

			e.printStackTrace();

		}
		return new Object[] { bw, fw };
	}

	public void appendStream(String data, BufferedWriter bw,boolean newLine) {
		if (data != null && bw != null)

			try {
				bw.write(data);
				if (newLine)bw.write("\r\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public void appendStream(List<String> datas, BufferedWriter bw) {
		if (datas != null && bw != null)
			for (String data : datas) {

				appendStream(data, bw,true);

			}

	}

	public void closeBuffer(BufferedWriter bw, FileWriter fw) {

		try {

			if (bw != null)
				bw.close();

			if (fw != null)
				fw.close();

		} catch (IOException ex) {

			ex.printStackTrace();

		}
	}

}
