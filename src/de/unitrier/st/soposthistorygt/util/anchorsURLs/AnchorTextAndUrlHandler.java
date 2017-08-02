package de.unitrier.st.soposthistorygt.util.anchorsURLs;

import org.apache.commons.text.StrBuilder;

import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlPair.*;
import static de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlPair.AnchorRefUrlType.anchorTextAndDirectURL;
import static de.unitrier.st.soposthistorygt.util.anchorsURLs.AnchorTextAndUrlPair.AnchorRefUrlType.anchorTextWithReferenceToURL;

public class AnchorTextAndUrlHandler {

    // TODO: Find a more efficient way to extract and normalize URLs if this takes too much time
    public Vector<AnchorTextAndUrlPair> extractAllAnchorsRefsAndURLpairs(String markdown){
        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = new Vector<>();

        markdown = markdown.replaceAll("\\s", " ");
        StrBuilder strBuilder = new StrBuilder(markdown);

        for(int i=0; i< AnchorTextAndUrlPair.AnchorRefUrlType.values().length; i++){
            AnchorTextAndUrlPair.AnchorRefUrlType tmpType = AnchorTextAndUrlPair.AnchorRefUrlType.values()[i];
            String tmpRegex = AnchorTextAndUrlPair.getRegexWithEnum(tmpType);

            if(tmpType == anchorTextWithReferenceToURL || tmpType == anchorTextAndDirectURL){
                Vector<AnchorTextAndUrlPair> andUrlPairs_inRoundAndSquareBrackets = parseAndExtractAnchorsReferencesURLsInSquareAndRoundBrackets(strBuilder);
                anchorTextAndUrlPairs.addAll(andUrlPairs_inRoundAndSquareBrackets);
                strBuilder = deleteAllAnchorsReferencesURLsOf(strBuilder, andUrlPairs_inRoundAndSquareBrackets);
                continue;
            }

            while(Pattern.matches(".*" + tmpRegex + ".*", strBuilder)){

                Scanner scanner = new Scanner(strBuilder.build());    //https://stackoverflow.com/a/1096628
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    AnchorTextAndUrlPair tmpAnchorTextAndUrlPair = parseLineToAnchorTextAndUrlPairs(line, tmpType);
                    if(null != tmpAnchorTextAndUrlPair){
                        anchorTextAndUrlPairs.add(tmpAnchorTextAndUrlPair);
                        assert tmpRegex != null;

                        strBuilder = new StrBuilder(strBuilder.build().replaceFirst(tmpRegex, ""));  // TODO: This seems to be the easiest way: to convert it to a string
                                                                                                        // because regex in Java is via Matcher anyway and returns a string.
                                                                                                        // So the solution is almost similar to this one: https://stackoverflow.com/a/17368032
                        // strBuilder = strBuilder.replaceFirst(tmpRegex, ""); // this replaces a char sequence and not a regex
                    }
                }
                scanner.close();
            }
        }

        return anchorTextAndUrlPairs;
    }


    private AnchorTextAndUrlPair parseLineToAnchorTextAndUrlPairs(String line, AnchorTextAndUrlPair.AnchorRefUrlType type){

        switch (type){
            case spanClassWithAnchorTextAndDirectURL:
                return parse_spanClassWithAnchorTextAndDirectURL(line);
            case bareURL_wrappedInTags:
                return parse_bareURL_wrappedInTags(line);
            case urlWrappedInHTMLsyntax:
                return parse_urlWrappedInHTMLsyntax(line);
            case anchorTextAndDirectURL:
                // do nothing... this will be handled in method extractAllAnchorsRefsAndURLpairs
                break;
            case anchorTextWithReferenceToURL:
                // do nothing... this will be handled in method extractAllAnchorsRefsAndURLpairs
                break;
            case bareURL_notWrappedInTags:
                return parse_bareURL_notWrappedInTags(line);
        }

        return null;
    }


    private AnchorTextAndUrlPair parse_spanClassWithAnchorTextAndDirectURL(String regexMatch){
        String spanContent = getStringsRowWiseByRegex(regex_spanClassWithAnchorTextAndDirectURL, regexMatch).firstElement();

        String anchor = getStringsRowWiseByRegex(
                "\\[.*?\\]",
                spanContent).firstElement();

        String url = getStringsRowWiseByRegex(
                regex_bareURL_notWrappedInTags,
                spanContent).firstElement();

        return new AnchorTextAndUrlPair(
                anchor.substring(1, anchor.length() - 1),
                null,
                url,
                AnchorTextAndUrlPair.AnchorRefUrlType.spanClassWithAnchorTextAndDirectURL
        );
    }

    private AnchorTextAndUrlPair parse_bareURL_wrappedInTags(String regexMatch){

        String url = getStringsRowWiseByRegex(
                regex_bareURL_wrappedInTags,
                regexMatch).firstElement();

        return new AnchorTextAndUrlPair(
                null,
                null,
                url.substring(1,url.length()-1),
                AnchorTextAndUrlPair.AnchorRefUrlType.bareURL_wrappedInTags
        );
    }

    private AnchorTextAndUrlPair parse_urlWrappedInHTMLsyntax(String regexMatch){

        String urlWithHtmlEnvironment = getStringsRowWiseByRegex(
                regex_urlWrappedInHTMLsyntax,
                regexMatch).firstElement();

        String urlBare = getStringsRowWiseByRegex(regex_bareURL_notWrappedInTags, urlWithHtmlEnvironment).firstElement();

        return new AnchorTextAndUrlPair(
                null,
                null,
                urlBare,
                AnchorRefUrlType.urlWrappedInHTMLsyntax
        );
    }

    private Vector<AnchorTextAndUrlPair> parseAndExtractAnchorsReferencesURLsInSquareAndRoundBrackets(StrBuilder markdown){

        Vector<AnchorTextAndUrlPair> anchorUrlPairs = new Vector<>();
        boolean inBrackets = false;
        StringBuilder tmp = new StringBuilder();

        for(int i=0; i<markdown.length(); i++){

            if(markdown.charAt(i) == '['){
                inBrackets = true;
            }
            else
            if(markdown.charAt(i) == ']'){
                inBrackets = false;
                if(i+1 < markdown.length() && markdown.charAt(i+1) == '['){
                    i = i+2;
                    StringBuilder ref = new StringBuilder();
                    while(i < markdown.length() && markdown.charAt(i) != ']'){
                        ref.append(markdown.charAt(i++));
                    }
                    i++;
                    anchorUrlPairs.add(new AnchorTextAndUrlPair(tmp.toString(), ref.toString(), null, anchorTextWithReferenceToURL));
                    tmp.setLength(0);   // https://stackoverflow.com/a/5192545
                }else if(i+1 < markdown.length() && markdown.charAt(i+1) == '('){
                    i = i+2;
                    StringBuilder url = new StringBuilder();
                    while(i < markdown.length() && markdown.charAt(i) != ')'){
                        url.append(markdown.charAt(i++));
                    }
                    i++;
                    anchorUrlPairs.add(new AnchorTextAndUrlPair(tmp.toString(), null, url.toString(), anchorTextAndDirectURL));
                    tmp.setLength(0);   // https://stackoverflow.com/a/5192545
                }
                else
                if(i+1 < markdown.length() && markdown.charAt(i+1) == ':'){
                    i = i+2;
                    for (AnchorTextAndUrlPair anchorUrlPair : anchorUrlPairs) {
                        if (null != anchorUrlPair.reference && anchorUrlPair.reference.equals(tmp.toString())) {
                            Scanner scanner = new Scanner(markdown.substring(i));    //https://stackoverflow.com/a/1096628
                            String line = scanner.nextLine();
                            String url = parse_bareURL_notWrappedInTags(line).url;
                            scanner.close();
                            anchorUrlPair.url = url;
                            break;
                        }
                    }
                    tmp.setLength(0);   // https://stackoverflow.com/a/5192545
                }
            }else if(inBrackets){
                tmp.append(markdown.charAt(i));
            }
        }


        return anchorUrlPairs;
    }

    private StrBuilder deleteAllAnchorsReferencesURLsOf(StrBuilder markdown, Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs){
        for(AnchorTextAndUrlPair anchorUrlPair : anchorTextAndUrlPairs){
            if(anchorUrlPair.type == anchorTextAndDirectURL){
                markdown = markdown.replaceAll("[" + anchorUrlPair.anchor + "](" + anchorUrlPair.url + ")", "");
            }else if(anchorUrlPair.type == anchorTextWithReferenceToURL){
                markdown = markdown.replaceAll("[" + anchorUrlPair.anchor + "][" + anchorUrlPair.reference + "]", "");
                markdown = markdown.replaceAll("[" + anchorUrlPair.reference + "]: " + anchorUrlPair.url, "");
            }
        }
        return markdown;
    }

    private AnchorTextAndUrlPair parse_bareURL_notWrappedInTags(String regexMatch){
        String url = getStringsRowWiseByRegex(
                regex_bareURL_notWrappedInTags,
                regexMatch).firstElement();

        return new AnchorTextAndUrlPair(
                null,
                null,
                url,
                AnchorRefUrlType.bareURL_notWrappedInTags
        );
    }


    private Vector<String> getStringsRowWiseByRegex(String regex, String markdownText){ // https://stackoverflow.com/a/6020436
        Vector<String> regexMatches = new Vector<>();
        Matcher m = Pattern.compile(regex).matcher(markdownText);
        while (m.find()) {
            regexMatches.add(m.group());
        }
        return regexMatches;
    }


    public String normalizeAnchorsRefsAndURLsForApp(String markdownText, Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs){

        // markdownText = markdownText.replaceAll("\\s", " ");

        for (AnchorTextAndUrlPair anchorTextAndUrlPair : anchorTextAndUrlPairs) {
            switch (anchorTextAndUrlPair.type){
                case anchorTextAndDirectURL:
                    // do nothing, this is the normalized form
                    break;

                case anchorTextWithReferenceToURL:      // this would go wrong if it appears before spanClassWithAnchorTextAndDirectURL, but spans are parsed before this type
                    markdownText = markdownText.replaceFirst(
                            "\\[" + anchorTextAndUrlPair.anchor + "\\]\\[" + anchorTextAndUrlPair.reference + "\\]",
                            "\\[" + anchorTextAndUrlPair.anchor + "\\]\\(" + anchorTextAndUrlPair.url + "\\)");

                    markdownText = markdownText.replaceFirst("\\[" + anchorTextAndUrlPair.reference + "\\]: " + regex_bareURL_notWrappedInTags + ".*", "");
                    break;

                case spanClassWithAnchorTextAndDirectURL:
                    markdownText = markdownText.replaceFirst(
                            regex_spanClassWithAnchorTextAndDirectURL,
                            "\\[" + anchorTextAndUrlPair.anchor + "\\]\\(" + anchorTextAndUrlPair.url + "\\)");
                    break;

                case urlWrappedInHTMLsyntax:
                    // do nothing, this would be the result after markup
                    break;

                case bareURL_notWrappedInTags:
                    markdownText = markdownText.replaceFirst(anchorTextAndUrlPair.url, "<" + anchorTextAndUrlPair.url + ">");
                    break;

                case bareURL_wrappedInTags:
                    // do nothing, the url will be marked up by pegdown
                    break;
            }
        }

        return markdownText;
    }

    public String normalizeAnchorsRefsAndURLsForDatabase(String markdownText, Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs){

        for (AnchorTextAndUrlPair anchorTextAndUrlPair : anchorTextAndUrlPairs) {
            switch (anchorTextAndUrlPair.type){
                case anchorTextAndDirectURL:
                    markdownText = markdownText.replaceFirst(
                            "\\[" + anchorTextAndUrlPair.anchor + "\\]\\(" + anchorTextAndUrlPair.url + "\\)",
                            anchorTextAndUrlPair.url);
                    break;

                case anchorTextWithReferenceToURL:      // this would go wrong if it appears before spanClassWithAnchorTextAndDirectURL, but spans are parsed before this type
                    markdownText = markdownText.replaceFirst(
                            "\\[" + anchorTextAndUrlPair.anchor + "\\]\\[" + anchorTextAndUrlPair.reference + "\\]",
                            anchorTextAndUrlPair.url);

                    markdownText = markdownText.replaceFirst("\\[" + anchorTextAndUrlPair.reference + "\\]: " + regex_bareURL_notWrappedInTags + ".*", "");
                    break;

                case spanClassWithAnchorTextAndDirectURL:
                    markdownText = markdownText.replaceFirst(
                            regex_spanClassWithAnchorTextAndDirectURL,
                            anchorTextAndUrlPair.url);
                    break;

                case urlWrappedInHTMLsyntax:
                    markdownText = markdownText.replaceFirst(
                            regex_urlWrappedInHTMLsyntax,
                            anchorTextAndUrlPair.url);
                    break;

                case bareURL_notWrappedInTags:
                    // do nothing, this is the normalized form for the database
                    break;

                case bareURL_wrappedInTags:
                    markdownText = markdownText.replaceFirst("<" + anchorTextAndUrlPair.url + ">", anchorTextAndUrlPair.url);
                    break;
            }
        }

        return markdownText;
    }

}
