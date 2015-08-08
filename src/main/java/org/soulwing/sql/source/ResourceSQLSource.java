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

import org.soulwing.sql.SQLRuntimeException;

/**
 * A {@link SQLSource} that reads from a resource.
 *
 * @author Carl Harris
 */
public class ResourceSQLSource extends ReaderSQLSource {

  public static final String DEFAULT_ENCODING = "UTF-8";

  /**
   * Constructs a new SQL source for a resource relative to the root of the
   * classpath, which uses the {@link #DEFAULT_ENCODING default encoding}.
   * <p>
   * The resource will be located using the thread context class loader.
   *
   * @param resourceName name of the resource
   */
  public ResourceSQLSource(String resourceName) {
    this(resourceName, DEFAULT_ENCODING);
  }

  /**
   * Constructs a new source SQL for a resource relative to the root of the
   * classpath, which uses the specified encoding.
   * <p>
   * The resource will be located using the thread context class loader.
   *
   * @param resourceName name of the resource
   * @param encoding character set name
   */
  public ResourceSQLSource(String resourceName, String encoding) {
    this(resourceName, Thread.currentThread().getContextClassLoader(), encoding);
  }

  /**
   * Constructs a new SQL source for a resource in the same package as the given
   * class which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param resourceName name of the resource
   * @param relativeToClass class that provides the package location of the
   *    resource
   */
  public ResourceSQLSource(String resourceName, Class<?> relativeToClass) {
    this(resourceName, relativeToClass, DEFAULT_ENCODING);
  }

  /**
   * Constructs a new SQL source for a resource in the same package as the given
   * class which uses the specified encoding.
   * @param resourceName name of the resource
   * @param relativeToClass class that provides the package location of the
   *    resource
   * @param encoding character set name
   */
  public ResourceSQLSource(String resourceName, Class<?> relativeToClass,
      String encoding) {
    this(resourceName, new ClassResourceAccessor(relativeToClass),
        encoding);
  }

  /**
   * Constructs a new SQL source for a resource relative to the root of the
   * given class loader, which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param resourceName name of the resource
   * @param classLoader class loader that will be used to access the resource
   */
  public ResourceSQLSource(String resourceName, ClassLoader classLoader) {
    this(resourceName, classLoader, DEFAULT_ENCODING);
  }

  /**
   * Constructs a new SQL source for a resource relative to the root of the
   * given class loader, which uses the specified encoding.
   * @param resourceName name of the resource
   * @param classLoader class loader that will be used to access the resource
   * @param encoding character set name
   */
  public ResourceSQLSource(String resourceName, ClassLoader classLoader,
      String encoding) {
    this(resourceName, new ClassLoaderResourceAccessor(classLoader),
        encoding);
  }

  /**
   * Constructs a new SQL source for a resource that will be obtained by the
   * given resource accessor and which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param resourceName name of the resource
   * @param accessor accessor that will be used to obtain a URL for the resource
   */
  public ResourceSQLSource(String resourceName, ResourceAccessor accessor) {
    this(resourceName, accessor, DEFAULT_ENCODING);
  }

  /**
   * Constructs a new SQL source for a resource that will be obtained by the
   * given resource accessor and which uses the specified encoding.
   * @param resourceName name of the resource
   * @param accessor accessor that will be used to obtain a URL for the resource
   * @param encoding character set name
   */
  public ResourceSQLSource(String resourceName, ResourceAccessor accessor,
      String encoding) {
    this(accessor.getResource(resourceName), encoding);
  }

  /**
   * Constructs a new SQL source for a resource at the specified location,
   * which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath which will be accessing using the thread context
   *    class loader
   */
  public ResourceSQLSource(URI location) {
    this(location, DEFAULT_ENCODING);
  }

  /**
   * Constructs a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath which will be accessing using the thread context
   *    class loader
   * @param encoding character set name
   */
  public ResourceSQLSource(URI location, String encoding) {
    super(openReader(translate(location), encoding));
  }

  /**
   * Constructs a new SQL source for a resource at the specified location,
   * which uses the {@link #DEFAULT_ENCODING default encoding}.
   * @param location location of the resource
   */
  public ResourceSQLSource(URL location) {
    this(location, DEFAULT_ENCODING);
  }

  /**
   * Constructs a new SQL source for a resource at the specified location,
   * which uses the specified encoding.
   * @param location location of the resource; this URL may use the
   *    {@code classpath:} scheme to specify a resource relative to the
   *    root of the classpath which will be accessing using the thread context
   *    class loader
   * @param encoding character set name
   */
  public ResourceSQLSource(URL location, String encoding) {
    super(openReader(location, encoding));
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
   * @param encoding en
   * @return
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
