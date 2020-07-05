::protoc --java_out=..\src\main\java\ p_common.proto
::protoc --java_out=..\src\main\java\ p_sm_scene.proto
::protoc --java_out=..\src\main\java\ p_auth_sm.proto

for /f "delims=" %%i in ('dir /b "*.proto"') do (

    D:\dev\protobuf3\bin\protoc --java_out=..\src\main\java\ %%i
)

pause