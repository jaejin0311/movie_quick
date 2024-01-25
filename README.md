- # General
    - #### Team#: Yolo

    - #### Names: Jaejin Kim

    - #### Project 5 Video Demo Link: https://drive.google.com/file/d/1SU2cAzzTqm7-jro7vQQCX9o89NgO_k0V/view?usp=sharing

    - #### Instruction of deployment: Nothing Specific!

    - #### Collaborations and Work Distribution: I have worked alone


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - WebContent/META-INF/context.xml (configuration)
    - src
        - AddMovieServlet.java
        - AddStarServlet.java
        - AutoCompleteServlet.java
        - CheckoutServlet.java
        - GenreMoviesServlet.java
        - LoginServlet.java
        - MoviesServlet.java
        - PrefixMoviesServlet.java
        - SearchMoviesServlet.java
        - SingleMovieServlet.java
        - SingleStarServlet.java
        - StarsServlet.java

    - #### Explain how Connection Pooling is utilized in the Fabflix code.
        - When the servlet needs to interact with the database, it borrows a connection from the dataSource.
        - Connections are reused from the pool rather than being created and closed for each database operation.
    - #### Explain how Connection Pooling works with two backend SQL.
        - When a component of your application needs to interact with a specific database, it borrows a connection from the corresponding DataSource.
        - After completing the database operations, return the connections to their respective connection pools.
        - Depending on the specific requirements or operations, your code should dynamically choose the appropriate DataSource to interact with the desired database.


- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    - src
        - AddMovieServlet.java
        - AddStarServlet.java
        - AutoCompleteServlet.java
        - CheckoutServlet.java
        - GenreMoviesServlet.java
        - LoginServlet.java
        - MoviesServlet.java
        - PrefixMoviesServlet.java
        - SearchMoviesServlet.java
        - SingleMovieServlet.java
        - SingleStarServlet.java
        - StarsServlet.java
    - #### How read/write requests were routed to Master/Slave SQL?
        - I couldn't figure this part completely, there has been errors, couldn't figure out.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.
        - I couldn't figure this part completely, there has been errors, couldn't figure out.
