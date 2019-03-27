# Image Saver plugin for Flutter
Saves photos to the gallery. Supports Android 16+ and IOS 8.0+


## Usage

``` dart
    import 'package:image_saver/image_saver.dart';


    void _onImageSaveButtonPressed() async {
      var response = await http
          .get('http://upload.art.ifeng.com/2017/0425/1493105660290.jpg');

      File savedFile = await ImageSaver.saveFile(fileData: response.bodyBytes);
      if (!mounted) {
        return;
      }
      setState(() {
        _imageFile = savedFile);
      });
    }

```