package com.jaiva.interpreter.libs.file;

import com.jaiva.interpreter.libs.BaseLibrary;
import com.jaiva.interpreter.libs.annotation.Exports;
import com.jaiva.interpreter.libs.annotation.JaivaLibrary;
import com.jaiva.interpreter.libs.file.bytes.FileBytes;
import com.jaiva.interpreter.libs.file.txt.FileApi;

@Exports({FileApi.class, FileBytes.class})
@JaivaLibrary(path = "file", description = "All the file related stuff, sorta all not sure if i did all while writing this annotation lololol")
public class FileExports extends BaseLibrary {
}
