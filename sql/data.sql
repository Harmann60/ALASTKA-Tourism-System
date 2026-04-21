USE alatska;

CREATE TABLE Country (
    CountryID INT PRIMARY KEY,
    CountryName VARCHAR(50)
);

CREATE TABLE City (
    CityID INT PRIMARY KEY,
    CityName VARCHAR(50),
    CountryID INT,
    FOREIGN KEY (CountryID) REFERENCES Country(CountryID)
);

CREATE TABLE Users (
    UserID INT PRIMARY KEY,
    Name VARCHAR(50),
    Email VARCHAR(50),
    Password VARCHAR(50)
);
create table TouristPlace_Before (
    PlaceID int,
    PlaceName varchar(50),
    Category varchar(100),
    CityID int
);
CREATE TABLE TouristPlace (
    PlaceID INT PRIMARY KEY,
    PlaceName VARCHAR(50),
    Category VARCHAR(30),
    CityID INT,
    FOREIGN KEY (CityID) REFERENCES City(CityID)
);

CREATE TABLE Accommodation (
    AccommodationID INT PRIMARY KEY,
    AccommodationName VARCHAR(50),
    Type VARCHAR(30),
    PricePerNight INT,
    CityID INT,
    FOREIGN KEY (CityID) REFERENCES City(CityID)
);

CREATE TABLE Booking (
    BookingID INT PRIMARY KEY,
    BookingDate DATE,
    UserID INT,
    AccommodationID INT,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (AccommodationID) REFERENCES Accommodation(AccommodationID)
);

CREATE TABLE Review (
    ReviewID INT PRIMARY KEY,
    Rating INT,
    Comment VARCHAR(100),
    UserID INT,
    PlaceID INT,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (PlaceID) REFERENCES TouristPlace(PlaceID)
);

INSERT INTO Country VALUES
(1,'India'),
(2,'Italy'),
(3,'Japan'),
(4,'Finland');

INSERT INTO City VALUES
(1,'Pune',1),
(2,'Mumbai',1),
(3,'Rome',2),
(4,'Venice',2),
(5,'Tokyo',3),
(6,'Osaka',3),
(7,'Helsinki',4);

INSERT INTO Users VALUES
(1,'Harman','harman@gmail.com','pass123'),
(2,'Ayush','ayush@gmail.com','pass456'),
(3,'Indrani','indrani@gmail.com','pass789'),
(4,'Jhanvi','jhanvi@gmail.com','pass999');

INSERT INTO TouristPlace_Before VALUES
(1,'Gateway of India','Historical',2),
(2,'Shaniwar Wada','Historical',1),
(3,'Colosseum','Historical',3),
(4,'Grand Canal','Adventure',4),
(5,'Shibuya Crossing','Adventure',5),
(6,'Osaka Castle','Historical',6),
(7,'Helsinki Cathedral','Historical, Religious',7);

INSERT INTO TouristPlace VALUES
(1,'Gateway of India','Historical',2),
(2,'Shaniwar Wada','Historical',1),
(3,'Colosseum','Historical',3),
(4,'Grand Canal','Adventure',4),
(5,'Shibuya Crossing','Adventure',5),
(6,'Osaka Castle','Historical',6),
(7,'Helsinki Cathedral','Historical',7);

INSERT INTO Accommodation VALUES
(1,'Hotel Pune Central','Hotel',2500,1),
(2,'Mumbai Residency','Hotel',3500,2),
(3,'Rome Suites','Hotel',4000,3),
(4,'Venice Stay','Resort',3000,4),
(5,'Tokyo Inn','Hotel',2800,5),
(6,'Osaka Comfort','Hotel',2600,6),
(7,'Helsinki Grand','Hotel',3200,7);

INSERT INTO Booking VALUES
(1,'2026-02-02',1,1),
(2,'2026-02-03',1,5),
(3,'2026-02-05',2,3),
(4,'2026-02-10',3,7);

INSERT INTO Review VALUES
(1,5,'Amazing place',1,1),
(2,4,'Loved it',2,3),
(3,5,'Beautiful city',3,7);
