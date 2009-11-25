/* Copyright 2001 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


/**
 * Caching version of the HTML serializer
 * @author Peter Kharchenko
 * @author <a href="mailto:arkin@exoffice.com">Assaf Arkin</a>
 * @see Serializer
 */
public final class CachingHTMLSerializer
    extends HTMLSerializer implements CachingSerializer
{

    CharacterCachingWriter cacher;
    String encoding;

    /**
     * Constructs a new serializer. The serializer cannot be used without
     * calling {@link #setOutputCharStream} or {@link #setOutputByteStream}
     * first.
     */
    public CachingHTMLSerializer()
    {
        super();
    }

    /**
     * Constructs a new HTML/XHTML serializer depending on the value of
     * <tt>xhtml</tt>. The serializer cannot be used without calling
     * init() first.
     *
     * @param xhtml True if XHTML serializing
     */
    protected CachingHTMLSerializer( boolean xhtml, OutputFormat format )
    {
        super( xhtml,format );
        if(format!=null) {
            this.encoding=format.getEncoding();
        }
    }

    /**
     * Constructs a new serializer. The serializer cannot be used without
     * calling {@link #setOutputCharStream} or {@link #setOutputByteStream}
     * first.
     */
    public CachingHTMLSerializer( OutputFormat format )
    {
        super(format);
        if(format!=null) {
            this.encoding=format.getEncoding();
        }
    }


    /**
     * Constructs a new serializer that writes to the specified writer
     * using the specified output format. If <tt>format</tt> is null,
     * will use a default output format.
     *
     * @param writer The writer to use
     * @param format The output format to use, null for the default
     */
    public CachingHTMLSerializer( Writer writer, OutputFormat format )
    {
        super(writer,format);
        CachingWriter cw=new CachingWriter(writer);
        this.cacher=cw;
        setOutputCharStream(cw);
        if(format!=null) {
            this.encoding=format.getEncoding();
        }
    }

    public void setOutputCharStream( Writer writer ) {
        CachingWriter cw=new CachingWriter(writer);
        this.cacher=cw;
        super.setOutputCharStream(cw);
    }

    /**
     * Constructs a new serializer that writes to the specified output
     * stream using the specified output format. If <tt>format</tt>
     * is null, will use a default output format.
     *
     * @param output The output stream to use
     * @param format The output format to use, null for the default
     */
    public CachingHTMLSerializer( OutputStream output, OutputFormat format )
    {
        super(output,format);
        CachingOutputStream cos=new CachingOutputStream(output);
        this.cacher=cos;
        setOutputByteStream( cos );
        if(format!=null) {
            this.encoding=format.getEncoding();
        }
    }

    public void setOutputByteStream( OutputStream output ) {
        CachingOutputStream cos=new CachingOutputStream(output);
        this.cacher=cos;
        super.setOutputByteStream(cos);
    }

    public void setOutputFormat( OutputFormat format )
    {
        super.setOutputFormat(format);
        if(format!=null) {
            this.encoding=format.getEncoding();
        }
    }

    // caching methods
    /**
     * When starting caching if we are inside an opening tag the ">" will
     * be written in order for the ">" to be included with the correct cache.
     * 
     * Normally the serializer doesn't know if a ">" or "/>" should be written
     * until some content is received or the tag is closed. When starting
     * caching after an opening tag the tag will be assumed to have some content
     * and will write out the ">" before starting the cache.
     */
    public boolean startCaching() throws IOException{
		content();
        _printer.flush();
        return cacher.startCaching();
    }
    
    /**
     * When stopping caching if we are inside an opening tag the ">" will
     * be written in order for the ">" to be included with the correct cache.
     * 
     * Normally the serializer doesn't know if a ">" or "/>" should be written
     * until some content is received or the tag is closed. When starting
     * caching after an opening tag the tag will be assumed to have some content
     * and will write out the ">" before starting the cache.
     */
    public boolean stopCaching() throws IOException {
		content();
        _printer.flush();
        return cacher.stopCaching(); 
    }

    public String getCache() throws UnsupportedEncodingException, IOException {
        _printer.flush();
        return cacher.getCache(this.encoding);
    }
    
    /**
     * Allows one to print a <code>String</code> of characters directly to the output stream.
     *
     * @param text a <code>String</code> value
     */
    public void printRawCharacters(String text) throws IOException{
        content();
        _printer.printText(text);
        //        _printer.flush();
    }

    /**
     * Let the serializer know if the document has already been started.
     *
     * @param setting a <code>boolean</code> value
     */
    public void setDocumentStarted(boolean setting) {
        _started=setting;
    }

    public void flush() throws IOException {
        _printer.flush();
        cacher.flush();
    }
        
}