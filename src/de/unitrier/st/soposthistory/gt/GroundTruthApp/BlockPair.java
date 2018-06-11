package de.unitrier.st.soposthistory.gt.GroundTruthApp;

import org.sotorrent.posthistoryextractor.diffs.LineDiff;
import org.sotorrent.posthistoryextractor.diffs.diff_match_patch;

import javax.swing.*;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class BlockPair {

    PostBlockWebView leftBlock;
    PostBlockWebView rightBlock;
    int leftVersion;

    BlockPair(
            PostBlockWebView leftBlock,
            PostBlockWebView rightBlock,
            int leftVersion) {

        this.leftBlock = leftBlock;
        this.rightBlock = rightBlock;
        this.leftVersion = leftVersion;
    }


    @Override
    public boolean equals(Object blockPair){
        return (blockPair instanceof BlockPair) && ((BlockPair) blockPair).leftBlock.equals(this.rightBlock)
                && ((BlockPair) blockPair).leftBlock.equals(this.rightBlock)
                && ((BlockPair) blockPair).leftVersion == this.leftVersion;
    }

    public String computeDiffs() {

        String string1 = leftBlock.postBlock.getContent();
        String string2 = rightBlock.postBlock.getContent();

        if(string1.trim().equals(string2.trim())){
            return "<html><head></head><body>" +
                    Controller.convertMarkdownToHTMLViaCommonmarkMark(
                            string2
                    )
                    + "</body></html>";
        }

        String uniqueLineDiffSeparator_left_start = "§§§§§§1";
        String uniqueLineDiffSeparator_right_start = "§§§§§§2";
        String uniqueLineDiffSeparator_left_end = "§§§§§§3";
        String uniqueLineDiffSeparator_right_end = "§§§§§§4";

        LineDiff lineDiff = new LineDiff();
        List<diff_match_patch.Diff> diffs = lineDiff.diff_lines_only(string1, string2);
        StringBuilder outputRightSb = new StringBuilder();

        for (diff_match_patch.Diff diff : diffs) {

            if (diff.operation == diff_match_patch.Operation.EQUAL) {
                if (!outputRightSb.toString().endsWith("\n"))
                    outputRightSb.append("\n");
                outputRightSb.append(diff.text);

            } else if (diff.operation == diff_match_patch.Operation.DELETE || diff.operation == diff_match_patch.Operation.INSERT) {
                String lineColor = (diff.operation == diff_match_patch.Operation.DELETE) ? "red" : (diff.operation == diff_match_patch.Operation.INSERT) ? "green" : "";

                StringTokenizer tokens = new StringTokenizer(diff.text, "\n");
                while (tokens.hasMoreTokens()) {
                    if (!outputRightSb.toString().endsWith("\n"))
                        outputRightSb.append("\n");
                    String tmpToken = tokens.nextToken();
                    int j = 0;
                    tmpToken = tmpToken.replace("\t", "    ");
                    while (j < tmpToken.length() && (tmpToken.charAt(j) == ' ' || tmpToken.charAt(j) == '\t')) {
                        outputRightSb.append(" ");
                        j++;
                    }
                    outputRightSb
                            .append(uniqueLineDiffSeparator_left_start)
                            .append("<span style=\"color:")
                            .append(lineColor).append("\">")
                            .append(uniqueLineDiffSeparator_left_end)
                            .append(tmpToken.substring(j))
                            .append(uniqueLineDiffSeparator_right_start)
                            .append("</span>")
                            .append(uniqueLineDiffSeparator_right_end)
                    ;
                }
            }

            if (!outputRightSb.toString().endsWith("\n")){
                if(leftBlock.postBlock.getPostBlockTypeId() == 1) {
                    outputRightSb.append("\n\n");
                } else if (rightBlock.postBlock.getPostBlockTypeId() == 1) {
                    outputRightSb.append("\n");
                }
            }
        }


        outputRightSb = new StringBuilder("<html><head></head><body>" +
                Controller.convertMarkdownToHTMLViaCommonmarkMark(
                        outputRightSb.toString()
                )
                + "</body></html>");



        String output = outputRightSb.toString();


        Pattern pattern_left = Pattern.compile(uniqueLineDiffSeparator_left_start + ".*" + uniqueLineDiffSeparator_left_end);
        Matcher matcher_left = pattern_left.matcher(output);

        while(matcher_left.find()) {
            String newStringLeft = matcher_left.group();
            newStringLeft = newStringLeft.replace("&lt;", "<");
            newStringLeft = newStringLeft.replace("&gt;", ">");
            newStringLeft = newStringLeft.replace("&quot;", "\"");

            output = output.replace(matcher_left.group(), newStringLeft);
        }



        Pattern pattern_right = Pattern.compile(uniqueLineDiffSeparator_right_start + ".*" + uniqueLineDiffSeparator_right_end);
        Matcher matcher_right = pattern_right.matcher(output);

        while(matcher_right.find()) {
            String newStringRight = matcher_right.group();

            newStringRight = newStringRight.replace("&lt;", "<");
            newStringRight = newStringRight.replace("&gt;", ">");
            newStringRight = newStringRight.replace("&quot;", "\"");

            output = output.replace(matcher_right.group(), newStringRight);
        }




        output = output.replace(uniqueLineDiffSeparator_left_start, "");
        output = output.replace(uniqueLineDiffSeparator_left_end, "");
        output = output.replace(uniqueLineDiffSeparator_right_start, "");
        output = output.replace(uniqueLineDiffSeparator_right_end, "");

        return output;
    }

}
