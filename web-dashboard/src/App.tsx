import { useState, useEffect } from 'react';
import { collection, onSnapshot, orderBy, query } from 'firebase/firestore';
import { signInAnonymously } from 'firebase/auth';
import { db, auth } from './firebase';
import type { SpeechEntry } from './types/SpeechEntry';
import {
  ThemeProvider,
  createTheme,
  CssBaseline,
  AppBar,
  Toolbar,
  Typography,
  Paper,
  Box,
  Chip,
  List,
  ListItem,
  Card,
  CardContent,
  Avatar,
  Fade,
  CircularProgress
} from '@mui/material';
import {
  RecordVoiceOver as RecordIcon,
  CheckCircle as ConnectedIcon,
  Error as DisconnectedIcon,
  AccessTime as TimeIcon
} from '@mui/icons-material';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#667eea',
    },
    secondary: {
      main: '#764ba2',
    },
    background: {
      default: '#0a0a0a',
      paper: 'rgba(255, 255, 255, 0.05)',
    },
  },
  typography: {
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen", "Ubuntu", "Cantarell", sans-serif',
    h4: {
      fontWeight: 600,
    },
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backdropFilter: 'blur(10px)',
        },
      },
    },
  },
});

function App() {
  const [speechEntries, setSpeechEntries] = useState<SpeechEntry[]>([]);
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(true);
  const [, setIsAuthenticated] = useState(false);

  useEffect(() => {
    // Authenticate first
    signInAnonymously(auth)
      .then(() => {
        setIsAuthenticated(true);
        
        // Then set up Firestore listener
        const q = query(
          collection(db, 'speechEntries'),
          orderBy('timestamp', 'desc')
        );

        const unsubscribe = onSnapshot(q, (querySnapshot) => {
          const entries: SpeechEntry[] = [];
          querySnapshot.forEach((doc) => {
            entries.push({
              id: doc.id,
              ...doc.data()
            } as SpeechEntry);
          });
          setSpeechEntries(entries);
          setIsConnected(true);
          setLoading(false);
        }, (error) => {
          console.error('Error listening to speech entries:', error);
          setIsConnected(false);
          setLoading(false);
        });

        return () => unsubscribe();
      })
      .catch((error) => {
        console.error('Authentication failed:', error);
        setIsConnected(false);
        setLoading(false);
      });
  }, []);

  const formatTimestamp = (timestamp: { seconds: number; nanoseconds: number }) => {
    const date = new Date(timestamp.seconds * 1000);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Box sx={{ 
        minHeight: '100vh',
        minWidth: '100vw',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      }}>
        <AppBar position="static" sx={{ backgroundColor: 'rgba(0,0,0,0.2)' }}>
          <Toolbar>
            <RecordIcon sx={{ mr: 2 }} />
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              实时语音文字显示
            </Typography>
            <Chip
              icon={isConnected ? <ConnectedIcon /> : <DisconnectedIcon />}
              label={isConnected ? '已连接' : '未连接'}
              color={isConnected ? 'success' : 'error'}
              variant="outlined"
            />
          </Toolbar>
        </AppBar>

        <Box sx={{ py: 4, px: 3, width: '100%' }}>
          {loading ? (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="50vh">
              <CircularProgress size={60} />
            </Box>
          ) : speechEntries.length === 0 ? (
            <Paper elevation={3} sx={{ p: 6, textAlign: 'center' }}>
              <RecordIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
              <Typography variant="h5" gutterBottom>
                等待语音输入...
              </Typography>
              <Typography variant="body1" color="text.secondary">
                请在Android应用中开始录音
              </Typography>
            </Paper>
          ) : (
            <List sx={{ width: '100%' }}>
              {speechEntries.map((entry, index) => (
                <Fade in={true} timeout={500} key={entry.id} style={{ transitionDelay: `${index * 100}ms` }}>
                  <ListItem sx={{ px: 0, mb: 2 }}>
                    <Card sx={{ width: '100%', overflow: 'visible' }}>
                      <CardContent sx={{ p: 3 }}>
                        <Box display="flex" alignItems="flex-start" gap={2}>
                          <Avatar sx={{ bgcolor: 'primary.main', mt: 0.5 }}>
                            <RecordIcon />
                          </Avatar>
                          <Box flexGrow={1}>
                            <Typography 
                              variant="h6" 
                              sx={{ 
                                fontSize: '2rem',
                                lineHeight: 1.6,
                                mb: 2,
                                wordBreak: 'break-word'
                              }}
                            >
                              {entry.text}
                            </Typography>
                            <Box display="flex" alignItems="center" gap={1}>
                              <TimeIcon sx={{ fontSize: 16, color: 'text.secondary' }} />
                              <Typography variant="caption" color="text.secondary">
                                {formatTimestamp(entry.timestamp)}
                              </Typography>
                            </Box>
                          </Box>
                        </Box>
                      </CardContent>
                    </Card>
                  </ListItem>
                </Fade>
              ))}
            </List>
          )}
        </Box>
      </Box>
    </ThemeProvider>
  );
}

export default App
