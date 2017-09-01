package de.unitrier.st.soposthistorygt.util;

import de.unitrier.st.soposthistory.blocks.PostBlockVersion;
import de.unitrier.st.soposthistory.version.PostVersion;
import de.unitrier.st.soposthistory.version.PostVersionList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class PostVersionListHelper {

    public static void makeDocument(PostVersionList postVersionList){
        try {
            int version = 0;
            for(PostVersion postVersion : postVersionList){

                PrintWriter printWriter = new PrintWriter(new File("./testdata/every block in own document/" + postVersion.getPostId() + "_" + (version++)));

                printWriter.write("PostId: " + postVersion.getPostId() + "\n");
                printWriter.write("Id: " + postVersion.getPostHistoryId() + "\n");
                printWriter.write("Version: " + version + "\n");
                printWriter.write("Amount of text blocks: " + postVersion.getTextBlocks().size() + "\n");
                printWriter.write("Amount of code blocks: " + postVersion.getCodeBlocks().size() + "\n");
                printWriter.write("\n\n");

                for(int i=0; i<postVersion.getPostBlocks().size(); i++) {

                    PostBlockVersion tmpPostBlock = postVersion.getPostBlocks().get(i);
                    String[] lines = tmpPostBlock.getContent().split("&#xD;&#xA;");
                    for (String line : lines) {
                        printWriter.write(line + "\n");
                    }
                    printWriter.write("\n\n");
                }
                printWriter.write("\n\n");

                printWriter.flush();
                printWriter.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
