/*
 * File created on Aug 5, 2015
 *
 * Copyright (c) 2015 Carl Harris, Jr
 * and others as noted
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.soulwing.sql.source;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * A {@link SQLSource} that reads from a resource.
 *
 * @author Carl Harris
 */
public class ResourceSQLSource extends ReaderSQLSource {

  public static final String DEFAULT_ENCODING = "UTF-8";

  /**
   * Constructs a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath which will be accessing using the thread context
   *    class loader
   * @param encoding character set name
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public ResourceSQLSource(URL location, String encoding) {
    super(openReader(location, encoding));
  }

  /**
   * Creates a new SQL source for a resource in the same package as the given
   * class, which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param name name of the resource
   * @param relativeToClass class that provides the package location of the
   *    resource
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String name, Class<?> relativeToClass) {
    return with(name, relativeToClass, DEFAULT_ENCODING);
  }

  /**
   * Creates a new SQL source for a resource in the same package as the given
   * class which uses the specified encoding.
   * @param name name of the resource
   * @param relativeToClass class that provides the package location of the
   *    resource
   * @param encoding character set name
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String name, Class<?> relativeToClass,
      String encoding) {
    return with(name, new ClassResourceAccessor(relativeToClass), encoding);
  }

  /**
   * Creates a new SQL source for a resource relative to the root of the
   * given class loader, which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param name name of the resource
   * @param classLoader class loader that will be used to access the resource
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String name, ClassLoader classLoader) {
    return with(name, classLoader, DEFAULT_ENCODING);
  }

  /**
   * Creates a new SQL source for a resource relative to the root of the
   * given class loader, which uses the specified encoding.
   * @param name name of the resource
   * @param classLoader class loader that will be used to access the resource
   * @param encoding character set name
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String name, ClassLoader classLoader,
      String encoding) {
    return with(name, new ClassLoaderResourceAccessor(classLoader), encoding);
  }

  /**
   * Creates a new SQL source for a resource that will be obtained from the
   * given resource accessor and which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param name name of the resource
   * @param accessor accessor that will be used to obtain a URL for the resource
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String name, ResourceAccessor accessor) {
    return with(name, accessor, DEFAULT_ENCODING);
  }

  /**
   * Creates a new SQL source for a resource that will be obtained from the
   * given resource accessor and which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param name name of the resource
   * @param accessor accessor that will be used to obtain a URL for the resource
   * @param encoding character set name
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String name, ResourceAccessor accessor,
      String encoding) {
    return with(accessor.getResource(name), encoding);
  }

  /**
   * Creates a new SQL source for a resource relative to the root of the
   * classpath, which uses the {@link #DEFAULT_ENCODING default encoding}.
   * <p>
   * The resource will be located using the thread context class loader.
   *
   * @param location URL for the resource; may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath (which will be accessed using the thread context
   *    class loader)
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String location) {
    return with(location, DEFAULT_ENCODING);
  }

  /**
   * Creates a new SQL source for a resource relative to the root of the
   * classpath, which uses the specified encoding.
   * <p>
   * The resource will be located using the thread context class loader.
   *
   * @param location URL for the resource; may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath (which will be accessed using the thread context
   *    class loader)
   * @param encoding character set name
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(String location, String encoding) {
    return with(URI.create(location), encoding);
  }

  /**
   * Creates a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath (which will be accessed using the thread context
   *    class loader)
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */

  public static ResourceSQLSource with(URI location) {
    return with(location, DEFAULT_ENCODING);
  }

  /**
   * Creates a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath (which will be accessed using the thread context
   *    class loader)
   * @param encoding character set name
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(URI location, String encoding) {
    return new ResourceSQLSource(translate(location), encoding);
  }

  /**
   * Creates a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath which will be accessing using the thread context
   *    class loader
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(URL location) {
    return new ResourceSQLSource(location, DEFAULT_ENCODING);
  }

  /**
   * Creates a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath which will be accessing using the thread context
   *    class loader
   * @param encoding character set name
   * @return SQL source
   * @throws SQLResourceNotFoundException if the specified resource is not found
   * @throws SQLInputException if an I/O errors when accessing the resource
   */
  public static ResourceSQLSource with(URL location, String encoding) {
    return new ResourceSQLSource(location, encoding);
  }

  /**
   * An accessor for a resource.
   * <p>
   * This is an interface that <em>should</em> be defined in the JDK and
   * implemented by the {@link Class} and {@link ClassLoader} objects.  Alas,
   * it is not.
   */
  public interface ResourceAccessor {

    /**
     * Gets a URL for a resource.
     * @param name name of the resource
     * @return location of the resource
     * @throws SQLResourceNotFoundException if the resource cannot be found
     */
    URL getResource(String name) throws SQLResourceNotFoundException;
  }

  /**
   * A {@link ResourceAccessor} based on a {@link ClassLoader}.
   */
  private static class ClassLoaderResourceAccessor implements ResourceAccessor {
    private final ClassLoader classLoader;

    public ClassLoaderResourceAccessor(ClassLoader classLoader) {
      this.classLoader = classLoader;
    }

    @Override
    public URL getResource(String name) {
      URL location = classLoader.getResource(name);
      if (location == null) {
        throw new SQLResourceNotFoundException(name);
      }
      return location;
    }

  }

  /**
   * A {@link ResourceAccessor} based on a {@link Class}.
   */
  private static class ClassResourceAccessor implements ResourceAccessor {
    private final Class clazz;

    public ClassResourceAccessor(Class clazz) {
      this.clazz = clazz;
    }

    @Override
    public URL getResource(String name) {
      URL location = clazz.getResource(name);
      if (location == null) {
        throw new SQLResourceNotFoundException(name, clazz);
      }
      return location;
    }

  }

  /**
   * Translates a URI to a URL.
   * <p>
   * The JDK URL class doesn't support the {@code classpath:} scheme, so we
   * translate it here, if necessary.
   *
   * @param uri the URL to translate
   * @return translated URL
   * @throws SQLResourceNotFoundException if a {@code classpath:} URL refers
   *    to a non-existent resource
   * @throws NullPointerException if {@code url} is {@code null}
   */
  private static URL translate(URI uri) {
    if (uri == null) {
      throw new NullPointerException("resource location is required");
    }
    if ("classpath".equals(uri.getScheme())) {
      String path = uri.getSchemeSpecificPart();
      if (path.startsWith("/")) {
        path = path.substring(1);
      }
      URL url = Thread.currentThread().getContextClassLoader()
          .getResource(path);
      if (url == null) {
        throw new SQLResourceNotFoundException(path);
      }
      return url;
    }
    try {
      return uri.toURL();
    }
    catch (MalformedURLException ex) {
      throw new SQLInputException(uri.toString(), ex);
    }
  }

  /**
   * Opens an input stream to a resource.
   * @param location location of the resource
   * @param encoding character set name
   * @return buffered reader for the stream
   */
  private static Reader openReader(URL location, String encoding) {
    try {
      InputStream inputStream = location.openStream();
      return new BufferedReader(new InputStreamReader(inputStream, encoding));
    }
    catch (FileNotFoundException ex) {
      throw new SQLResourceNotFoundException(location.toString());
    }
    catch (IOException ex) {
      throw new SQLInputException(ex.getMessage(), ex);
    }

  }

}
