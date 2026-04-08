CREATE DATABASE banking_sys;
USE banking_sys;
CREATE TABLE Accounts(account_no VARCHAR(20) PRIMARY KEY,holder VARCHAR(100) NOT NULL,
					  balance DOUBLE DEFAULT 0.0 CHECK(balance>=0), acc_type ENUM('SAVINGS','CURRENT') NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE Users(username VARCHAR(50) PRIMARY KEY, password VARCHAR(50) NOT NULL,
				   account_no VARCHAR(20) NOT NULL UNIQUE,
                   FOREIGN KEY (account_no) REFERENCES Accounts(account_no) ON DELETE CASCADE);
CREATE TABLE Transactions(trans_id INT AUTO_INCREMENT PRIMARY KEY, account_no  VARCHAR(20) NOT NULL,
						  trans_type ENUM('DEPOSIT','WITHDRAWAL','TRANSFER') NOT NULL,amount DOUBLE NOT NULL,
                          recipient_acc VARCHAR(20), recipient_name VARCHAR(100),
                          trans_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY(account_no) REFERENCES Accounts(account_no));