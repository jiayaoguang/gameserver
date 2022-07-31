::D:\dev\protobuf3\bin\protoc --java_out=..\src\main\java\ MsgBytes.proto

for /f "delims=" %%i in ('dir /b "*.proto"') do (

    D:\dev\protobuf3\bin\protoc --java_out=..\src\main\java\ %%i
)

pause


pause